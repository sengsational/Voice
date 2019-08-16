package de.ph1b.audiobook.features.bookOverview.list

import android.view.ViewGroup
import androidx.core.view.isVisible
import de.ph1b.audiobook.R
import de.ph1b.audiobook.data.Book
import de.ph1b.audiobook.misc.RoundRectOutlineProvider
import de.ph1b.audiobook.misc.dpToPx
import de.ph1b.audiobook.misc.formatTime
import de.ph1b.audiobook.misc.recyclerComponent.AdapterComponent
import de.ph1b.audiobook.uitools.ExtensionsHolder
import kotlinx.android.synthetic.main.book_overview_row_list.*
import timber.log.Timber

class GridBookOverviewComponent(private val listener: BookClickListener) :
  AdapterComponent<BookOverviewModel, BookOverviewHolder>(BookOverviewModel::class) {

  override val viewType = 42

  override fun onCreateViewHolder(parent: ViewGroup): BookOverviewHolder {
    return BookOverviewHolder(
      layoutRes = R.layout.book_overview_row_grid,
      parent = parent,
      listener = listener
    )
  }

  override fun onBindViewHolder(model: BookOverviewModel, holder: BookOverviewHolder) {
    holder.bind(model)
  }

  override fun isForViewType(model: Any): Boolean {
    return model is BookOverviewModel && model.useGridView
  }
}

class ListBookOverviewComponent(private val listener: BookClickListener) :
  AdapterComponent<BookOverviewModel, BookOverviewHolder>(BookOverviewModel::class) {

  override val viewType = 43

  override fun onCreateViewHolder(parent: ViewGroup): BookOverviewHolder {
    return BookOverviewHolder(
      layoutRes = R.layout.book_overview_row_list,
      parent = parent,
      listener = listener
    )
  }

  override fun onBindViewHolder(model: BookOverviewModel, holder: BookOverviewHolder) {
    holder.bind(model)
  }

  override fun isForViewType(model: Any): Boolean {
    return model is BookOverviewModel && !model.useGridView
  }
}

class BookOverviewHolder(
  layoutRes: Int,
  parent: ViewGroup,
  private val listener: BookClickListener
) : ExtensionsHolder(parent, layoutRes) {

  private var boundBook: Book? = null
  private val loadBookCover = LoadBookCover(this)

  init {
    val outlineProvider = RoundRectOutlineProvider(itemView.context.dpToPx(2F))
    itemView.clipToOutline = true
    itemView.outlineProvider = outlineProvider
    cover.clipToOutline = true
    cover.outlineProvider = outlineProvider
    itemView.setOnClickListener {
      boundBook?.let { book ->
        listener(book, BookOverviewClick.REGULAR)
      }
    }
    itemView.setOnLongClickListener {
      boundBook?.let { book ->
        listener(book, BookOverviewClick.MENU)
        true
      } ?: false
    }
  }

  fun bind(model: BookOverviewModel) {
    boundBook = model.book
    val name = model.name
    title.text = name
    if (model.useGridView) {
      title.maxLines = 2
    } else {
      author.text = model.author
      var fileName = model.book.content.currentFile.name
      var fileNameUpper = model.book.content.currentFile.name.toUpperCase()

      var authorTextUpper = model.author?.toUpperCase()
      var locAuthor = fileNameUpper.indexOf(authorTextUpper.toString())
      var titleTextUpper = model.name.toUpperCase()
      var locTitle = fileNameUpper.indexOf(titleTextUpper.toString())
      if (locAuthor > -1) {
        fileName = fileName.substring(locAuthor + (authorTextUpper?.length ?: 0)).trim()
      } else if (locTitle > -1) {
        fileName = fileName.substring(locTitle + titleTextUpper.length).trim()
      }

      author.text = fileName
      author.isVisible = fileName != null
      title.maxLines = if (fileName == null) 2 else 1
    }

    cover.transitionName = model.transitionName
    remainingTime.text = formatTime(model.remainingTimeInMs.toLong())
    this.progress.progress = model.progress
    loadBookCover.load(model.book)

    playingIndicator.isVisible = model.isCurrentBook
  }
}
