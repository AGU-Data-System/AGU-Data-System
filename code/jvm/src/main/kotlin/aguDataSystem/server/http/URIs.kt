package aguDataSystem.server.http

import org.springframework.web.util.UriTemplate
import java.net.URI

object URIs {

    const val PREFIX = "/api"
    const val HOME = "$PREFIX/"

    fun home() = URI(HOME)

    /**
     * Contains the URIs for the agu endpoints
     */
    object Agu {
        const val ROOT = "$PREFIX/agus"
        const val GET_BY_ID = "/{aguId}"
        const val CREATE = "/create"

        fun byID(id: Int) = UriTemplate(ROOT + GET_BY_ID).expand(id)
        fun home() = URI(HOME)
        fun register() = URI(CREATE)
    }

}