import java.io._
import scala.annotation.tailrec

object Main {

  def main(args: Array[String]): Unit = {
    // Read binary file from source path
    val sourcePath = "/home/ashish/Downloads/Movie/Everything.Everywhere.All.At.Once.2022.1080p.WebDL.H264.AC3.Will1869.mp4"
    val sourceFile = new File(sourcePath)
    val fileSize = sourceFile.length()

    // Store the size of the binary file in bytes
    println(s"File size: $fileSize bytes")

    // Ask user what they want to do
    println("What do you want to do?")
    println("1. Divide file into blocks")
    println("2. Merge blocks into file")
    println("3. Display paths and names of divided blocks")
    val choice = scala.io.StdIn.readInt()

    if (choice == 1) {
      // Divide file into blocks
      val destPath = "/home/ashish/Documents/File Distribution System Blocks"
      val destFolder = new File(destPath)
      if (!destFolder.exists()) {
        destFolder.mkdirs()
      }
      val blockBytes = 100 * 1024 * 1024
      val blockSize = blockBytes.toInt
      val numBlocks = (fileSize.toDouble / blockBytes).ceil.toInt
      val input = new FileInputStream(sourceFile)
      try {
        divideFile(input, blockSize, destFolder, numBlocks)
      } catch {
        case ex: IOException => throw ex
      } finally {
        input.close()
      }
    } else if (choice == 2) {
      // Merge blocks into file
      val destPath = "/home/ashish/Downloads/Movie/"
      val destFile = new File(destPath)
      val blockPaths = getBlockPaths(destFile.getParentFile())
      mergeFiles(blockPaths, destFile)
    } else if (choice == 3) {
      // Display paths and names of divided blocks
      val destPath = "/home/ashish/Documents/File Distribution System Blocks"
      val blockPaths = getBlockPaths(new File(destPath))
      blockPaths.foreach(println)
    } else {
      println("Invalid choice")
    }
  }

  // Divide file into blocks
  def divideFile(input: FileInputStream, blockSize: Int, destFolder: File, numBlocks: Int): Unit = {
    val buffer = new Array[Byte](blockSize)
    var bytesRead = 0
    var blockNumber = 1
    var output: FileOutputStream = null
    try {
      while (bytesRead >= 0 && blockNumber <= numBlocks) {
        bytesRead = input.read(buffer)
        if (bytesRead > 0) {
          val blockFile = new File(destFolder, s"block_$blockNumber")
          output = new FileOutputStream(blockFile)
          output.write(buffer, 0, bytesRead)
          output.close()
          output = null
          blockNumber += 1
        }
      }
    } catch {
      case ex: IOException => throw ex
    } finally {
      try {
        input.close()
      } catch {
        case ex: IOException => throw ex
      }
      if (output != null) {
        try {
          output.close()
        } catch {
          case ex: IOException => throw ex
        }
      }
    }
  }

  // Merge blocks into file
  def mergeFiles(blockPaths: List[String], destFile: File, blockSize: Int): Unit = {
  val output = new BufferedOutputStream(new FileOutputStream(destFile))
  try {
    blockPaths.foreach { blockPath =>
      val input = new BufferedInputStream(new FileInputStream(blockPath))
      try {
        val buffer = new Array[Byte](blockSize)
        var bytesRead = input.read(buffer)
        while (bytesRead != -1) {
          output.write(buffer, 0, bytesRead)
          bytesRead = input.read(buffer)
        }
      } finally {
        input.close()
      }
    }
  } finally {
    output.close()
  }
}

