package com.smarte2e.unit;

import com.smarte2e.exceptions.SmartValidationException;
import com.smarte2e.utils.TextUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TextUtilsTest {
    @Test
    public void testSplitMultilineStringWithUnixNewline() {
        String text = "Line1\nLine2\nLine3";
        String[] lines = TextUtils.splitMultilineString(text);

        Assert.assertEquals(lines.length, 3);
        Assert.assertEquals(lines[0], "Line1");
        Assert.assertEquals(lines[1], "Line2");
        Assert.assertEquals(lines[2], "Line3");
    }

    @Test
    public void testSplitMultilineStringWithWindowsNewline() {
        String text = "Line1\r\nLine2\r\nLine3";
        String[] lines = TextUtils.splitMultilineString(text);

        Assert.assertEquals(lines.length, 3);
        Assert.assertEquals(lines[0], "Line1");
        Assert.assertEquals(lines[1], "Line2");
        Assert.assertEquals(lines[2], "Line3");
    }

    @Test
    public void testSplitMultilineStringWithOldMacNewline() {
        String text = "Line1\rLine2\rLine3";
        String[] lines = TextUtils.splitMultilineString(text);

        Assert.assertEquals(lines.length, 3);
        Assert.assertEquals(lines[0], "Line1");
        Assert.assertEquals(lines[1], "Line2");
        Assert.assertEquals(lines[2], "Line3");
    }

    @Test
    public void testSplitMultilineStringWithNewlineAndCarriageReturn() {
        String text = "Line1\n\rLine2\n\rLine3";
        String[] lines = TextUtils.splitMultilineString(text);
        Assert.assertEquals(lines.length, 3);
        Assert.assertEquals(lines[0], "Line1");
        Assert.assertEquals(lines[1], "Line2");
        Assert.assertEquals(lines[2], "Line3");
    }

    @Test
    public void testSplitMultilineStringWithMixedNewlines() {
        String text = "Line1\nLine2\r\nLine3\rLine4";
        String[] lines = TextUtils.splitMultilineString(text);

        Assert.assertEquals(lines.length, 4);
        Assert.assertEquals(lines[0], "Line1");
        Assert.assertEquals(lines[1], "Line2");
        Assert.assertEquals(lines[2], "Line3");
        Assert.assertEquals(lines[3], "Line4");
    }

    @Test
    public void testSplitMultilineStringWithSingleLine() {
        String text = "SingleLine";
        String[] lines = TextUtils.splitMultilineString(text);
        Assert.assertEquals(lines.length, 1);
        Assert.assertEquals(lines[0], "SingleLine");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testSplitMultilineStringWithNullInput() {
        TextUtils.splitMultilineString(null);
    }

    @Test
    public void testSplitMultilineStringWithEmptyInput() {
        String[] lines = TextUtils.splitMultilineString("");

        Assert.assertEquals(lines.length, 1);
        Assert.assertEquals(lines[0], "");
    }

    @Test
    public void testSplitMultilineStringWithBlankInput() {
        String[] lines = TextUtils.splitMultilineString(" ");

        Assert.assertEquals(lines.length, 1);
        Assert.assertEquals(lines[0], " ");
    }

    @Test
    public void testSplitMultilineStringWithNewLineCharacterOnlyInput() {
        String[] lines = TextUtils.splitMultilineString("\n");

        Assert.assertEquals(lines.length, 2);
        Assert.assertEquals(lines[0], "");
        Assert.assertEquals(lines[1], "");
    }

    @Test
    public void testSplitMultilineStringWithCarriageReturnCharacterOnlyInput() {
        String[] lines = TextUtils.splitMultilineString("\r");

        Assert.assertEquals(lines.length, 2);
        Assert.assertEquals(lines[0], "");
        Assert.assertEquals(lines[1], "");
    }

    @Test
    public void testSplitMultilineStringWithNewLineAndCarriageReturnCharactersOnlyInput() {
        String[] lines = TextUtils.splitMultilineString("\n\r");

        Assert.assertEquals(lines.length, 2);
        Assert.assertEquals(lines[0], "");
        Assert.assertEquals(lines[1], "");
    }
}
