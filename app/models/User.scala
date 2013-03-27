package models

import java.util.UUID

case class User(uuid: UUID, group: Group, realname: String, email: User.Email, password: String, twitter: String)


object User {

  case class Email(val value: String) {
    override val toString = value
  }

  def anonymous(uuid: UUID = UUID.randomUUID()) = User(uuid, Group.Anonymous(), "anonymous", Email("anonymous@anonymous"), "", "@anonymous")

}

