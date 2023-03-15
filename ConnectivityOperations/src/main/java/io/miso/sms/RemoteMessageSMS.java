package io.miso.sms;

import io.miso.core.WorkOperation;
import io.miso.exceptions.InvalidSMSMessage;
import io.miso.util.DataOutputHandler;
import io.miso.util.SecurityUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Remote Message protocol (RM-protocol 0.5) using AES-128 for encryption and HMAC-SHA256 for authentication
 * <p>
 * Field	Size (bytes)	Description
 * HMAC-SHA256	32	Message Authentication Code to verify the authenticity and integrity of the message.
 * Encrypted Header	18	Encrypted header containing WorkOperation ID (2), timestamp (12), and origin ID (4).
 * Encrypted Payload	74	Encrypted payload containing data specific to the WorkOperation.
 * <p>
 * Total message size: 32 (HMAC) + 18 (Header) + 74 (Payload) = 124 bytes
 */
public class RemoteMessageSMS {
    private static final Logger logger = LogManager.getFormatterLogger();
    private static final int MAXIMUM_SIZE_HEADER_BYTES = 50;
    private static final int MAXIMUM_SIZE_PAYLOAD_BYTES = 74;

    private final WorkOperation workOperation;
    private final DataOutputHandler buffer;


    public RemoteMessageSMS(final WorkOperation workOperation) {
        this.workOperation = workOperation;
        this.buffer = new DataOutputHandler();
        compose(); // Initialize this objects data. Essentially creating the actual SMS message
    }

    private void compose() {
        composeHeader();
        composePayload();
        encryptMessage();
        insertHMAC();
        validate(); // Validate message
    }

    public byte[] getMessage() {
        return buffer.toByteArray();
    }

    private void composeHeader() {
        try {
            buffer.writeUnsignedShort(workOperation.getOperationCommand().getId());
            buffer.writeUnsignedLong(workOperation.getCreatedAt().toEpochMilli()
                    * 1_000_000_000L + workOperation.getCreatedAt().getNano());
            buffer.writeUnsignedInt(workOperation.getClusterId());
            logger.debug("Done composing header for [%s]. Size of buffer: %d", workOperation.toString(), buffer.size());
        } catch (final IOException e) {
            logger.error("Failed I/O operation with DataOutputHandler while composing header!", e);
        }
    }

    private void composePayload() {
        try {
            buffer.write(workOperation.getPayload());
            logger.debug("Done composing payload for [%s]. Size of buffer: %d", workOperation.toString(), buffer.size());
        } catch (final IOException e) {
            logger.error("Failed I/O operation with DataOutputHandler while composing payload!", e);
        }
    }

    private void encryptMessage() {
        try {
            final byte[] data = buffer.toByteArray(); // Save current buffer to data array
            buffer.reset(); // Reset buffer of all data

            final byte[] encryptedData = SecurityUtil.encryptData(data); // Encrypt with AES-128 CBC algorithm
            buffer.write(encryptedData); // Write the saved data so that it comes after the secret
            logger.debug("Done encrypting message for [%s]. Size of buffer: %d", workOperation.toString(), buffer.size());
        } catch (final IOException e) {
            logger.error("Failed I/O operation with DataOutputHandler!", e);
        }
    }

    private void insertHMAC() {
        try {
            final byte[] data = buffer.toByteArray(); // Save current buffer to data array
            buffer.reset(); // Reset buffer of all data

            final byte[] secret = SecurityUtil.composeHMACSecret(new byte[]{0x0}, data); // Use saved data for secret
            buffer.write(secret); // Write secret byte array to the beginning of the newly reset buffer
            buffer.write(data); // Write the saved data so that it comes after the secret
            logger.debug("Done inserting HMAC at the start of the message for [%s]. Size of buffer: %d", workOperation.toString(), buffer.size());
        } catch (final IOException e) {
            logger.error("Failed I/O operation with DataOutputHandler!", e);
        }
    }

    private void validate() {
        if (buffer.size() > MAXIMUM_SIZE_HEADER_BYTES + MAXIMUM_SIZE_PAYLOAD_BYTES) {
            throw new InvalidSMSMessage(String.format("Invalid size! Total size of buffer is %d and total allowed size is %s... ",
                    buffer.size(), (MAXIMUM_SIZE_HEADER_BYTES + MAXIMUM_SIZE_PAYLOAD_BYTES)));
        }
    }
}
