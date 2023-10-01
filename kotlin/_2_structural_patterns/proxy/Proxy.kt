package _2_structural_patterns.proxy

import java.net.URL

data class CatImage(
    val thumbnailUrl: String,
    val url: String,
) {
    val image: ByteArray by lazy {
        //이미지를 바이트 배열로 읽는다.
        URL(url).readBytes()
    }
}
