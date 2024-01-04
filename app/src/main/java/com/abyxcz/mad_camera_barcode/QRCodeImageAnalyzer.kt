package com.abyxcz.mad_camera_barcode

import android.graphics.ImageFormat
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.BinaryBitmap

import com.google.zxing.ChecksumException

import com.google.zxing.FormatException

import com.google.zxing.NotFoundException

import com.google.zxing.PlanarYUVLuminanceSource

import com.google.zxing.Result

import com.google.zxing.common.HybridBinarizer

import com.google.zxing.multi.qrcode.QRCodeMultiReader


class QRCodeImageAnalyzer(private val listener: QRCodeFoundListener) : ImageAnalysis.Analyzer {
    override fun analyze(image: ImageProxy) {
        if (image.format == ImageFormat.YUV_420_888 || image.format == ImageFormat.YUV_422_888 || image.format == ImageFormat.YUV_444_888) {
            val byteBuffer = image.planes[0].buffer
            val imageData = ByteArray(byteBuffer.capacity())
            byteBuffer[imageData]
            val source = PlanarYUVLuminanceSource(
                imageData,
                image.width, image.height,
                0, 0,
                image.width, image.height,
                false
            )
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            try {
                val result: Result = QRCodeMultiReader().decode(binaryBitmap)
                listener.onQRCodeFound(result.getText())
            } catch (e: FormatException) {
                listener.qrCodeNotFound()
            } catch (e: ChecksumException) {
                listener.qrCodeNotFound()
            } catch (e: NotFoundException) {
                listener.qrCodeNotFound()
            }
        }
        image.close()
    }
}