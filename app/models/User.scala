package models

import java.util.UUID

case class User(uuid: UUID, group: Group, realname: String, email: User.Email, password: String, twitter: String) {
  def withPassword(newPassword: String) =
    User(uuid, group, realname, email, newPassword, twitter)
}

object User {

  case class Email(val value: String) {
    override val toString = value
  }

  def anonymous(uuid: UUID = UUID.randomUUID()) = User(uuid, Group.Anonymous(), "anonymous", Email("anonymous@anonymous"), "", "@anonymous")

}

