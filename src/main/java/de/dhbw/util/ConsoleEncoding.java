package de.dhbw.util;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public final class ConsoleEncoding {
    private ConsoleEncoding() {
    }

    public static void configureUtf8Console() {
        System.setOut(utf8PrintStream(new FileOutputStream(FileDescriptor.out)));
        System.setErr(utf8PrintStream(new FileOutputStream(FileDescriptor.err)));
    }

    static PrintStream utf8PrintStream(OutputStream outputStream) {
        return new PrintStream(outputStream, true, StandardCharsets.UTF_8);
    }
}
