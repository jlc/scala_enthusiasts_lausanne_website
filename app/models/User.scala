package models

case class User(group: Group, realname: String, email: String, twitter: String)

object User {
  def anonymous = User(Group.Anonymous(), "anonymous", "anonymous@anonymous", "@anonymous")
}
