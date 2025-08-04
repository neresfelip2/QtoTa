package br.com.qtota.data.mapper

import br.com.qtota.ui.screen.menu.User
import com.auth0.jwt.JWT

object UserMapper {

    fun String.tokenToUser() : User {
        val claims = JWT().decodeJwt(this).claims

        return User(
            id = claims["sub"]!!.asString().toLong(),
            name = claims["name"]!!.asString(),
            email = claims["email"]!!.asString()
        )
    }

}