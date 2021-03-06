package org.mauritania.photosync.starter.gui

import javafx.scene.control.Tooltip
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.{control => javafxcontrol}

import org.mauritania.photosync.olympus.sync.SyncPlanItem.DownloadedStatus
import org.mauritania.photosync.olympus.sync.SyncPlanItem
import org.mauritania.photosync.starter.gui.CustomCell.CellType
import javafx.util.Callback

import scalafx.scene.image.ImageView

class CustomCell extends javafxcontrol.ListCell[CellType] {

  override def updateItem(item: CellType, empty: Boolean): Unit = {
    super.updateItem(item, empty)
    if (!empty) {
      val fileDate = item.fileInfo.getHumanDate
      val fileSize = item.fileInfo.size
      val fileDir = item.fileInfo.folder
      val fileName = item.fileInfo.name
      val fileNameDir = s"$fileDir/$fileName"
      val downloadStatus = item.downloadStatus
      val downloadStatusCode = asCode(downloadStatus)
      val fileThumbnail = item.fileInfo.thumbnailUrl
      val toolTipText =
        s"""File:  $fileNameDir
           |Size:  $fileSize bytes
           |Date:  $fileDate
           |Status: $downloadStatus""".stripMargin
      setText(fileNameDir + " [" + downloadStatusCode + "]")
      setTooltip(new Tooltip(toolTipText))
      fileThumbnail match {
        case Some(t) => setGraphic(new ImageView(t.toString))
        case None => setGraphic(getRectangle(downloadStatus))
      }
    } else {
      setText(null)
      setTooltip(null)
      setGraphic(null)
    }
  }

  def getRectangle(downloadStatus: DownloadedStatus): Rectangle = {
    val statusColor = asColor(downloadStatus)
    val rect = new Rectangle(20, 20)
    rect.setFill(statusColor)
    rect.setAccessibleText(downloadStatus.toString)
    rect
  }

  private def asColor(downloadStatus: DownloadedStatus) = {
    val statusColor = downloadStatus match {
      case SyncPlanItem.PartiallyDownloaded => Color.ORANGE
      case SyncPlanItem.Downloaded => Color.GREEN
      case SyncPlanItem.OnlyLocal => Color.WHITE
      case SyncPlanItem.OnlyRemote => Color.RED
    }
    statusColor
  }

  private def asCode(downloadStatus: DownloadedStatus) = {
    val statusColor = downloadStatus match {
      case SyncPlanItem.PartiallyDownloaded => "KO: broken locally"
      case SyncPlanItem.Downloaded => "OK: in PC and in camera"
      case SyncPlanItem.OnlyLocal => "OK: only in PC"
      case SyncPlanItem.OnlyRemote => "KO: only in camera"
    }
    statusColor
  }
}

object CustomCell {

  type CellType = SyncPlanItem

  val CustomCellCallback = new Callback[javafxcontrol.ListView[CellType], javafxcontrol.ListCell[CellType]] {
    def call(param: javafxcontrol.ListView[CellType]): javafxcontrol.ListCell[CellType] = {
      return new CustomCell()
    }
  }

}
