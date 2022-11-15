package users.domain

import cats.kernel.Eq
import io.circe.Encoder

final case class UserName(value: String) extends AnyVal

object UserName {
  implicit val eq: Eq[UserName] =
    Eq.fromUniversalEquals

  implicit val userNameCirceEncoder: Encoder[UserName] = Encoder[String].contramap(_.value)
}
