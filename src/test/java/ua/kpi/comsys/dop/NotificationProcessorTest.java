package ua.kpi.comsys.dop;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("NotificationProcessor and Domain Model Tests")
class NotificationProcessorTest {

    private NotificationProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new NotificationProcessor();
    }

    @Nested
    @DisplayName("NotificationProcessor Logic")
    class ProcessorTests {

        @Test
        @DisplayName("Should process TextMsg correctly")
        void shouldProcessTextMsg() {
            Notification notification = new TextMsg("Alice", "Hello, World!");
            String expected = """
                    Notification: TEXT
                    From: Alice
                    Message: Hello, World!""";

            assertEquals(expected, processor.process(notification));
        }

        @Test
        @DisplayName("Should process ImageMsg correctly")
        void shouldProcessImageMsg() {
            Notification notification = new ImageMsg("Bob", "https://cdn.example.com/avatar.png", 2048);
            String expected = """
                    Notification: IMAGE
                    From: Bob
                    URL: https://cdn.example.com/avatar.png (2048 KB)""";

            assertEquals(expected, processor.process(notification));
        }

        @Test
        @DisplayName("Should process AudioMsg correctly")
        void shouldProcessAudioMsg() {
            Notification notification = new AudioMsg("Charlie", 145);
            String expected = """
                    Notification: AUDIO
                    From: Charlie
                    Duration: 145 seconds""";

            assertEquals(expected, processor.process(notification));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when notification is null")
        void shouldThrowWhenNotificationIsNull() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> processor.process(null)
            );
            assertEquals("Notification cannot be null", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("TextMsg Validation")
    class TextMsgValidationTests {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   ", "\t", "\n"})
        @DisplayName("Should reject invalid sender in TextMsg")
        void shouldRejectInvalidSender(String invalidSender) {
            assertThrows(IllegalArgumentException.class, () -> new TextMsg(invalidSender, "Valid text"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   ", "\t", "\n"})
        @DisplayName("Should reject invalid text in TextMsg")
        void shouldRejectInvalidText(String invalidText) {
            assertThrows(IllegalArgumentException.class, () -> new TextMsg("Valid sender", invalidText));
        }
    }

    @Nested
    @DisplayName("ImageMsg Validation")
    class ImageMsgValidationTests {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   ", "\t"})
        @DisplayName("Should reject invalid sender in ImageMsg")
        void shouldRejectInvalidSender(String invalidSender) {
            assertThrows(IllegalArgumentException.class, () -> new ImageMsg(invalidSender, "https://test.com/img.jpg", 100));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   ", "\t"})
        @DisplayName("Should reject invalid URL in ImageMsg")
        void shouldRejectInvalidUrl(String invalidUrl) {
            assertThrows(IllegalArgumentException.class, () -> new ImageMsg("Valid sender", invalidUrl, 100));
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1, -500})
        @DisplayName("Should reject non-positive sizeKb in ImageMsg")
        void shouldRejectNonPositiveSize(int invalidSize) {
            assertThrows(IllegalArgumentException.class, () -> new ImageMsg("Valid sender", "https://test.com/img.jpg", invalidSize));
        }
    }

    @Nested
    @DisplayName("AudioMsg Validation")
    class AudioMsgValidationTests {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   ", "\t"})
        @DisplayName("Should reject invalid sender in AudioMsg")
        void shouldRejectInvalidSender(String invalidSender) {
            assertThrows(IllegalArgumentException.class, () -> new AudioMsg(invalidSender, 60));
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1, -120})
        @DisplayName("Should reject non-positive durationSeconds in AudioMsg")
        void shouldRejectNonPositiveDuration(int invalidDuration) {
            assertThrows(IllegalArgumentException.class, () -> new AudioMsg("Valid sender", invalidDuration));
        }
    }
}