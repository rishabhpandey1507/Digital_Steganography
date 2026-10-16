package digitalsteganography;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Steganography {

    public static BufferedImage encode(
            BufferedImage original,
            String message) throws Exception {

        byte[] messageBytes =
                message.getBytes(
                        StandardCharsets.UTF_8
                );

        byte[] lengthBytes =
                ByteBuffer
                        .allocate(4)
                        .putInt(messageBytes.length)
                        .array();

        byte[] data =
                new byte[
                        4 + messageBytes.length
                        ];

        System.arraycopy(
                lengthBytes,
                0,
                data,
                0,
                4
        );

        System.arraycopy(
                messageBytes,
                0,
                data,
                4,
                messageBytes.length
        );

        int capacity =
                original.getWidth()
                        * original.getHeight()
                        * 3;

        if (data.length * 8 > capacity) {

            throw new Exception(
                    "Message is too large for this image."
            );
        }

        BufferedImage image =
                new BufferedImage(
                        original.getWidth(),
                        original.getHeight(),
                        BufferedImage.TYPE_INT_RGB
                );

        for (int y = 0;
             y < original.getHeight();
             y++) {

            for (int x = 0;
                 x < original.getWidth();
                 x++) {

                image.setRGB(
                        x,
                        y,
                        original.getRGB(x, y)
                );
            }
        }

        int bitIndex = 0;

        for (int y = 0;
             y < image.getHeight();
             y++) {

            for (int x = 0;
                 x < image.getWidth();
                 x++) {

                int rgb =
                        image.getRGB(x, y);

                int red =
                        (rgb >> 16) & 0xFF;

                int green =
                        (rgb >> 8) & 0xFF;

                int blue =
                        rgb & 0xFF;

                int[] channels = {
                        red,
                        green,
                        blue
                };

                for (int c = 0;
                     c < 3;
                     c++) {

                    if (bitIndex <
                            data.length * 8) {

                        int byteIndex =
                                bitIndex / 8;

                        int bitPosition =
                                7 -
                                        (bitIndex % 8);

                        int bit =
                                (data[byteIndex]
                                        >> bitPosition)
                                        & 1;

                        channels[c] =
                                (channels[c] & 0xFE)
                                        | bit;

                        bitIndex++;
                    }
                }

                int newRgb =
                        (channels[0] << 16)
                                | (channels[1] << 8)
                                | channels[2];

                image.setRGB(
                        x,
                        y,
                        newRgb
                );
            }
        }

        return image;
    }

    public static String decode(
            BufferedImage image)
            throws Exception {

        int[] headerBits =
                new int[32];

        int bitIndex = 0;

        boolean headerComplete = false;

        for (int y = 0;
             y < image.getHeight() &&
                     !headerComplete;
             y++) {

            for (int x = 0;
                 x < image.getWidth() &&
                         !headerComplete;
                 x++) {

                int rgb =
                        image.getRGB(x, y);

                int[] channels = {
                        (rgb >> 16) & 0xFF,
                        (rgb >> 8) & 0xFF,
                        rgb & 0xFF
                };

                for (int c = 0;
                     c < 3;
                     c++) {

                    if (bitIndex < 32) {

                        headerBits[bitIndex] =
                                channels[c] & 1;

                        bitIndex++;

                    } else {

                        headerComplete = true;
                        break;
                    }
                }
            }
        }

        int messageLength = 0;

        for (int i = 0; i < 32; i++) {

            messageLength =
                    (messageLength << 1)
                            | headerBits[i];
        }

        int maximumBytes =
                (image.getWidth()
                        * image.getHeight()
                        * 3)
                        / 8;

        if (messageLength < 0 ||
                messageLength >
                        maximumBytes - 4) {

            throw new Exception(
                    "No valid hidden message found."
            );
        }

        byte[] messageBytes =
                new byte[messageLength];

        int currentBit = 0;

        int totalBits =
                (messageLength + 4) * 8;

        for (int y = 0;
             y < image.getHeight() &&
                     currentBit < totalBits;
             y++) {

            for (int x = 0;
                 x < image.getWidth() &&
                         currentBit < totalBits;
                 x++) {

                int rgb =
                        image.getRGB(x, y);

                int[] channels = {
                        (rgb >> 16) & 0xFF,
                        (rgb >> 8) & 0xFF,
                        rgb & 0xFF
                };

                for (int c = 0;
                     c < 3 &&
                             currentBit < totalBits;
                     c++) {

                    int bit =
                            channels[c] & 1;

                    if (currentBit >= 32) {

                        int dataBit =
                                currentBit - 32;

                        int byteIndex =
                                dataBit / 8;

                        int position =
                                7 -
                                        (dataBit % 8);

                        messageBytes[byteIndex] |=
                                (byte)
                                        (bit << position);
                    }

                    currentBit++;
                }
            }
        }

        return new String(
                messageBytes,
                StandardCharsets.UTF_8
        );
    }
}