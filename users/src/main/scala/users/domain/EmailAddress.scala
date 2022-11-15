package users.domain

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

final case class EmailAddress(value: String) extends AnyVal
object EmailAddress {
  implicit val emailAddressCirceEncoder: Encoder[EmailAddress] = deriveEncoder
}