package models

import java.util.{UUID, Date => JDate}

import play.Logger

import com.shorrockin.cascal.utils.Conversions._
import com.shorrockin.cascal.session._

import controllers.DB

object ContentDao {
  import DaoParams._
  import DaoHelper._

  def getMiscContent(mcType: MCType): Option[MiscContent] = DB { session =>
    if (session.count(ks \ CF.MiscContent \ byteBuffer(mcType.value)) == 0) {
      None
    } else {
      val values = session.list(ks \ CF.MiscContent \ byteBuffer(mcType.value), ColumnPredicate(List("date", "title", "content")))
      val vs = mapColumns(values)

      val date = new JDate(vs.getOrElse("date", "0").toLong)

      Some(MiscContent(
        mcType, date,
        vs.getOrElse("title", ""),
        vs.getOrElse("content", "")
      ))
    }
  }

  def saveMiscContent(mc: MiscContent) { DB { session =>
    val key = ks \ CF.MiscContent \ byteBuffer(mc.mcType.value)
    val values =
      Insert(key \ ("date", mc.date.getTime.toString)) ::
      Insert(key \ ("title", mc.title)) ::
      Insert(key \ ("content", mc.content)) :: Nil

    session.batch(values)
  }}

  def getEnthusiastSession(date: JDate): Option[EnthusiastSession] = DB { session =>
    if (session.count(ks \ CF.EnthusiastSession \ byteBuffer(date)) == 0)
      None
    else {
      val values = session.list(ks \ CF.MiscContent \ byteBuffer(date), ColumnPredicate(List("speaker", "title", "content")))
      val vs = mapColumns(values)

      Some(EnthusiastSession(
        date,
        vs.getOrElse("title", ""),
        vs.getOrElse("speaker", ""),
        vs.getOrElse("content", "")
      ))
    }
  }

  def saveEnthusiastSession(eSession: EnthusiastSession) { DB { session =>
    val key = ks \ CF.EnthusiastSession \ byteBuffer(eSession.date)
    val values =
      Insert(key \ ("title", eSession.title)) ::
      Insert(key \ ("speaker", eSession.title)) ::
      Insert(key \ ("content", eSession.content)) :: Nil

    session.batch(values)
  }}

}
