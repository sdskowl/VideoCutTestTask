package com.example.videocuttesttask.features.converter

import android.content.Context
import android.net.Uri
import android.util.Log
import com.arthenica.ffmpegkit.*
import com.example.videocuttesttask.features.models.ConverterState
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File

class ConverterImpl(private val context: Context) : Converter {
    private val TAG = "ConverterImpl"
    private var sessionConvert: FFmpegSession? = null
    private var sessionMedia: MediaInformationSession? = null
    override val converterState: MutableStateFlow<ConverterState?> = MutableStateFlow(null)

    /**
     * Here we take media info and send command
     * */
    override fun convert(uri: Uri) {
        sessionMedia = FFprobeKit.getMediaInformationAsync(prepareUri(uri)) { ses ->
            val returnCode = ses.returnCode
            when {
                returnCode.isValueSuccess -> {

                    val duration = ses.mediaInformation.duration.toDouble()
                    val inputVideoUri: String = prepareUri(uri)
                    val averageMs = duration / 2
                    val folder = context.cacheDir
                    val file = File(folder, System.currentTimeMillis().toString() + ".mp4")
                    val exe =
                        "-y -i $inputVideoUri -filter_complex " +
                                "[0:v]trim=${averageMs}:${duration},setpts=PTS-STARTPTS[v1];" +
                                "[0:a]atrim=${averageMs}:${duration},asetpts=PTS-STARTPTS[a1];" +
                                "[0:v]trim=0:${averageMs},setpts=PTS-STARTPTS[v2];" +
                                "[0:a]atrim=0:${averageMs},asetpts=PTS-STARTPTS[a2];" +
                                "[v1][a1][v2][a2]concat=n=2:v=1:a=1[outv][outa] -map '[outv]' -map '[outa]' -crf 0 -preset superfast ${file.absolutePath}"
                    startCommand(exe, file.absolutePath)


                }
                returnCode.isValueError -> {
                    converterState.value = ConverterState.Error("Can't read media info")
                }
            }
        }

    }

    /**
     * Convert unsupported uri
     * */
    private fun prepareUri(uri: Uri): String {
        return FFmpegKitConfig.getSafParameterForRead(context, uri)
    }

    /**
     * Cancel sessions.
     * */
    override fun cancel() {
        sessionConvert?.cancel()
        sessionMedia?.cancel()
    }

    /**
     * Executer commands and send states
     * */
    private fun startCommand(exe: String, filePathInCash: String) {

        sessionConvert = FFmpegKit.executeAsync(exe, { fFmpegSession ->
            Log.d(TAG, fFmpegSession.allLogsAsString)
            val returnCode = fFmpegSession.returnCode
            when {
                returnCode.isValueSuccess -> {
                    converterState.value = ConverterState.Video(filePathInCash)
                }
                returnCode.isValueError -> {
                    converterState.value = ConverterState.Error("Something went wrong")
                }
            }
            Log.d(TAG, "returnCode = $returnCode")
        }, { log ->
            Log.d(TAG, "log = ${log.message}")

        }, { s ->
            Log.d(TAG, "static = $s")
            converterState.value = ConverterState.Progress(s.toString())
        }
        )
    }
}