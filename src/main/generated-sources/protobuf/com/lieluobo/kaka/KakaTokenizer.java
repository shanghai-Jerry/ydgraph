// AUTOGENERATED FILE - DO NOT MODIFY!
// This file generated by Djinni from kaka.djinni

package com.lieluobo.kaka;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class KakaTokenizer {
    public abstract ArrayList<String> tokenizeString(String words, boolean longword);

    public abstract ArrayList<WordT> tokenizeWithPosition(String words);

    public static native boolean initConfig(String conf);

    public static native KakaTokenizer newInstance();

    private static final class CppProxy extends KakaTokenizer
    {
        private final long nativeRef;
        private final AtomicBoolean destroyed = new AtomicBoolean(false);

        private CppProxy(long nativeRef)
        {
            if (nativeRef == 0) throw new RuntimeException("nativeRef is zero");
            this.nativeRef = nativeRef;
        }

        private native void nativeDestroy(long nativeRef);
        public void destroy()
        {
            boolean destroyed = this.destroyed.getAndSet(true);
            if (!destroyed) nativeDestroy(this.nativeRef);
        }
        protected void finalize() throws java.lang.Throwable
        {
            destroy();
            super.finalize();
        }

        @Override
        public ArrayList<String> tokenizeString(String words, boolean longword)
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_tokenizeString(this.nativeRef, words, longword);
        }
        private native ArrayList<String> native_tokenizeString(long _nativeRef, String words, boolean longword);

        @Override
        public ArrayList<WordT> tokenizeWithPosition(String words)
        {
            assert !this.destroyed.get() : "trying to use a destroyed object";
            return native_tokenizeWithPosition(this.nativeRef, words);
        }
        private native ArrayList<WordT> native_tokenizeWithPosition(long _nativeRef, String words);
    }
}