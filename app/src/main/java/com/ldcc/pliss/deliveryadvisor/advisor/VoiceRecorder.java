/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ldcc.pliss.deliveryadvisor.advisor;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;

/**
 * 음성을 계속하여 연속으로 녹음하고 음성 (또는 소리)가 들리면 {@link VoiceRecorder.Callback}에 알립니다.
 *
 * <p>녹음된 오디오 형식은 항상 {@link AudioFormat#ENCODING_PCM_16BIT} 와 {@link AudioFormat#CHANNEL_IN_MONO}.
 *  이 클래스는 자동으로 장치의 올바른 샘플 속도를 선택합니다. 선택된 Sample late를 얻으려면 {@link #getSampleRate()}를 이용하면 됩니다. </p>
 */
public class VoiceRecorder {

    /** Sample late 후보 군 */
    private static final int[] SAMPLE_RATE_CANDIDATES = new int[]{16000, 11025, 22050, 44100};

    private static final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private static final int AMPLITUDE_THRESHOLD = 1500;
    private static final int SPEECH_TIMEOUT_MILLIS = 2000;
    private static final int MAX_SPEECH_LENGTH_MILLIS = 30 * 1000;

    public static abstract class Callback {

        /**
         * recorder가 음성 청취를 start 했을 때 호출됩니다.
         */
        public void onVoiceStart() {
        }

        /**
         * recorder가 음성 청취 중일 때 호출됩니다.
         *
         * @param data The audio data in {@link AudioFormat#ENCODING_PCM_16BIT}.
         * @param size The size of the actual data in {@code data}.
         */
        public void onVoice(byte[] data, int size) {
        }

        /**
         * recorder가 음성을 청취하는 걸 stop 했을 때 호출됩니다.
         */
        public void onVoiceEnd() {
        }
    }

    private final Callback mCallback;

    private AudioRecord mAudioRecord;

    private Thread mThread;

    private byte[] mBuffer;

    private final Object mLock = new Object();

    /** 음성의 끝이 들렸을 때의 시각 */
    private long mLastVoiceHeardMillis = Long.MAX_VALUE;

    /** 현재의 음성이 시작되는 시각입니다. */
    private long mVoiceStartedMillis;

    public VoiceRecorder(@NonNull Callback callback) {
        mCallback = callback;
    }

    /**
     * 오디오 녹음을 시작합니다.
     *
     * <p>이 함수의 호출 진원지는 나중에 {@link #stop()} 를 꼭 호출해야 합니다.</p>
     */
    public void start() {
        // 현재 진행 중이던 녹음이 있으면, 이를 중지
        stop();
        // 새로운 녹음 세션 만들기 시도
        mAudioRecord = createAudioRecord();
        if (mAudioRecord == null) {
            throw new RuntimeException("Cannot instantiate VoiceRecorder");
        }
        // 녹음 시작
        mAudioRecord.startRecording();
        // 캡처되는 오디오 처리를 시작
        mThread = new Thread(new ProcessVoice());
        mThread.start();
    }

    /**
     * 오디오 녹음을 중지합니다.
     */
    public void stop() {
        synchronized (mLock) {
            dismiss();
            if (mThread != null) {
                mThread.interrupt();
                mThread = null;
            }
            if (mAudioRecord != null) {
                mAudioRecord.stop();
                mAudioRecord.release();
                mAudioRecord = null;
            }
            mBuffer = null;
        }
    }

    public Thread getThread(){
        return mThread;
    }

    /**
     * 현재 진행 중인 음성을 닫습니다.
     */
    public void dismiss() {
        if (mLastVoiceHeardMillis != Long.MAX_VALUE) {
            mLastVoiceHeardMillis = Long.MAX_VALUE;
            mCallback.onVoiceEnd();
        }
    }

    /**
     * 오디오를 녹음하기 위해서 현재 사용되고 있는 Sample rate를 얻어 옵니다.
     *
     * @return 녹음된 오디오의 Sample late.
     */
    public int getSampleRate() {
        if (mAudioRecord != null) {
            return mAudioRecord.getSampleRate();
        }
        return 0;
    }

    /**
     * 새로운 {@link AudioRecord} 생성하여 반환합니다.
     *
     * @return 새롭게 만들어진 {@link AudioRecord}, 또는 권한이 없을 경우 null
     */
    private AudioRecord createAudioRecord() {
        for (int sampleRate : SAMPLE_RATE_CANDIDATES) {
            final int sizeInBytes = AudioRecord.getMinBufferSize(sampleRate, CHANNEL, ENCODING);
            if (sizeInBytes == AudioRecord.ERROR_BAD_VALUE) {
                continue;
            }
            final AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    sampleRate, CHANNEL, ENCODING, sizeInBytes);
            if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                mBuffer = new byte[sizeInBytes];
                return audioRecord;
            } else {
                audioRecord.release();
            }
        }
        return null;
    }

    /**
     * 캡처된 오디오를 계속 처리해서 이벤트를 {@link #mCallback}에 통보합니다.
     */
    private class ProcessVoice implements Runnable {

        @Override
        public void run() {
            while (true) {
                //synchronized (mLock) {
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                    final int size = mAudioRecord.read(mBuffer, 0, mBuffer.length);
                    final long now = System.currentTimeMillis();
                    if (isHearingVoice(mBuffer, size)) {
                        if (mLastVoiceHeardMillis == Long.MAX_VALUE) {
                            mVoiceStartedMillis = now;
                            mCallback.onVoiceStart();
                        }
                        mCallback.onVoice(mBuffer, size);
                        mLastVoiceHeardMillis = now;
                        if (now - mVoiceStartedMillis > MAX_SPEECH_LENGTH_MILLIS) {
                            end();
                        }
                    } else if (mLastVoiceHeardMillis != Long.MAX_VALUE) {
                        mCallback.onVoice(mBuffer, size);
                        if (now - mLastVoiceHeardMillis > SPEECH_TIMEOUT_MILLIS) {
                            end();
                        }
                    }
                //}
            }
        }

        private void end() {
            mLastVoiceHeardMillis = Long.MAX_VALUE;
            mCallback.onVoiceEnd();
        }

        private boolean isHearingVoice(byte[] buffer, int size) {
            for (int i = 0; i < size - 1; i += 2) {
                // The buffer has LINEAR16 in little endian.
                int s = buffer[i + 1];
                if (s < 0) s *= -1;
                s <<= 8;
                s += Math.abs(buffer[i]);
                if (s > AMPLITUDE_THRESHOLD) {
                    return true;
                }
            }
            return false;
        }
    }
}
