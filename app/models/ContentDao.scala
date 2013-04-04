package models

import java.util.{UUID, Date => JDate}

import play.Logger
import play.api.i18n.Lang

import com.shorrockin.cascal.utils.Conversions._
import com.shorrockin.cascal.session._

import controllers.DB

object ContentDao {
  import DaoParams._
  import DaoHelper._

  def getMiscContent(mcType: MiscContent.Type, lang: Lang): Option[MiscContent] = DB { session =>
    if (session.count(ks \ CF.MiscContent \ makeKey(mcType, lang)) == 0) {
      None
    } else {
      val values = session.list(ks \ CF.MiscContent \ makeKey(mcType, lang), ColumnPredicate(List("date", "title", "content")))
      val vs = mapColumns(values)

      val date = new JDate(vs.getOrElse("date", "0").toLong)

      Some(MiscContent(
        lang, mcType, date,
        vs.getOrElse("title", ""),
        vs.getOrElse("content", "")
      ))
    }
  }

  def saveMiscContent(mc: MiscContent) { DB { session =>
    val key = ks \ CF.MiscContent \ makeKey(mc.mcType, mc.lang)
    val values =
      Insert(key \ ("date", mc.date.getTime.toString)) ::
      Insert(key \ ("title", mc.title)) ::
      Insert(key \ ("content", mc.content)) :: Nil

    session.batch(values)
  }}

  def getEnthusiastSession(date: JDate, lang: Lang): Option[EnthusiastSession] = DB { session =>
    if (session.count(ks \ CF.EnthusiastSession \ makeKey(date, lang)) == 0)
      None
    else {
      val values = session.list(ks \ CF.EnthusiastSession \ byteBuffer(date), ColumnPredicate(List("speaker", "title", "content")))
      val vs = mapColumns(values)

      Some(EnthusiastSession(
        lang, date,
        vs.getOrElse("title", ""),
        vs.getOrElse("speaker", ""),
        vs.getOrElse("content", "")
      ))
    }
  }

  def saveEnthusiastSession(eSession: EnthusiastSession) { DB { session =>
    val key = ks \ CF.EnthusiastSession \ makeKey(eSession.date, eSession.lang)
    val values =
      Insert(key \ ("title", eSession.title)) ::
      Insert(key \ ("speaker", eSession.title)) ::
      Insert(key \ ("content", eSession.content)) :: Nil

    session.batch(values)
  }}

  private def makeKey(mcType: MiscContent.Type, lang: Lang) = byteBuffer(mcType.value + "_" + lang.code)

  private def makeKey(date: JDate, lang: Lang) = byteBuffer(date.getTime + "_" + lang.code)

}
