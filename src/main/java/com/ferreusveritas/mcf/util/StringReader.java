package com.ferreusveritas.mcf.util;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * A custom string reader made for reading strings. Based off Mojang's {@link com.mojang.brigadier.StringReader} but customised
 * for this mod's purposes.
 */
public class StringReader {

    private String string;
    private int cursor = 0;

    public StringReader(String string) {
        this.string = string;
    }

    public char read() {
        if (cursor >= string.length()) {
            throw new StringIndexOutOfBoundsException();
        }
        return string.charAt(cursor++);
    }

    public String read(int offset) {
        if (cursor + offset > string.length()) {
            throw new StringIndexOutOfBoundsException();
        }
        String returnString = string.substring(cursor, cursor + offset);
        this.cursor += returnString.length();
        return returnString;
    }

    public String readUntil(char character) {
        StringBuilder stringBuilder = new StringBuilder();
        while (hasRemaining() && peek() != character) {
            stringBuilder.append(read());
        }
        return stringBuilder.toString();
    }

    public String readUntil(char character, Character... stopAfter) {
        StringBuilder stringBuilder = new StringBuilder();
        Set<Character> stopAfterChars = Sets.newHashSet(stopAfter);
        while (hasRemaining()) {
            char next = peek();
            if (next == character) {
                break;
            } else if (stopAfterChars.contains(next)) {
                skip();
                break;
            }
            skip();
            stringBuilder.append(next);
        }
        return stringBuilder.toString();
    }

    public String readUntilAndConsume(char character) {
        String returnString = readUntil(character);
        if (hasRemaining()) {
            this.read();
        }
        return returnString;
    }

    public String readUntilAndConsume(char character, Character... stopAfter) {
        StringBuilder stringBuilder = new StringBuilder();
        Set<Character> stopAfterChars = Sets.newHashSet(stopAfter);
        while (hasRemaining()) {
            char next = read();
            if (next == character || stopAfterChars.contains(next)) {
                break;
            }
            stringBuilder.append(next);
        }
        return stringBuilder.toString();
    }

    public void skip() {
        this.cursor++;
    }

    public void skipWhile(char character) {
        while (hasRemaining() && peek() == character) {
            skip();
        }
    }

    public void skipUntil(char character) {
        while (hasRemaining() && read() == character) {
        }
    }

    public char peek() {
        return peek(0);
    }

    public char peekLast() {
        return peek(-1);
    }

    public char peek(int offset) {
        if (cursor + offset >= string.length()) {
            throw new StringIndexOutOfBoundsException();
        }
        return string.charAt(cursor + offset);
    }

    public boolean hasRemaining() {
        return cursor < string.length();
    }

    public int getCursor() {
        return cursor;
    }

    public void setCursor(int cursor) {
        this.cursor = cursor;
    }

    public void resetCursor() {
        this.cursor = 0;
    }

    public String getRemaining() {
        String returnString = string.substring(cursor);
        this.cursor = string.length();
        return returnString;
    }

    public String getFullString() {
        return string;
    }
}

