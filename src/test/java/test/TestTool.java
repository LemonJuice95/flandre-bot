package test;

import io.lemonjuice.flandre_bot.api.RandomTouhouImage;
import org.junit.jupiter.api.Test;

public class TestTool {
    @Test
    public void testTouhouImage() {
        RandomTouhouImage.get("");
    }
}
