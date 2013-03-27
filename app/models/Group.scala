package models

import play.Logger

abstract class Group(val ty: Int, val name: String) {
  override def toString = name
}

object Group {
  case class Anonymous() extends Group(0, "Anonymous")
  case class User() extends Group(1, "User")
  case class God() extends Group(2, "God")

  def fromName(grp: String): Group = grp.trim match {
    case "User" => User()
    case "God" => God()
    case _ => Anonymous()
  }
}

