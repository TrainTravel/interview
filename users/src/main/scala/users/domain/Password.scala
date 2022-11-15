package users.domain

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

final case class Password(value: String) extends AnyVal
object Password {
  implicit val passwordCirceEncoder: Encoder[Password] = deriveEncoder
}