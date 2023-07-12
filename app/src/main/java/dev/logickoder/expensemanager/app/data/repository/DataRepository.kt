package dev.logickoder.expensemanager.app.data.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import dev.logickoder.expensemanager.app.data.model.DataHeader
import dev.logickoder.expensemanager.app.data.model.DataRow
import dev.logickoder.expensemanager.app.data.source.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DataRepository(database: AppDatabase) {
    private val scope = CoroutineScope(Job() + Dispatchers.IO)
    private val dao = database.dao()
    private var lastSortHeader: DataHeader? = null
    private var query = startingQuery

    val headers = MutableStateFlow(
        database.columnNames(DataRow.TableName).map {
            DataHeader(it, DataHeader.SortType.None)
        }.run {
            subList(1, lastIndex)
        }
    )

    val data = MutableStateFlow(emptyList<DataRow>())

    init {
        scope.launch {
            sort(lastSortHeader)
        }
    }

    suspend fun add(data: DataRow) = withContext(Dispatchers.IO) {
        dao.insert(data)
        sort(lastSortHeader)
    }

    suspend fun query(query: String) = withContext(Dispatchers.IO) {
        val queriedData = dao.runtimeQuery(SimpleSQLiteQuery(query))
        data.emit(queriedData)
        this@DataRepository.query = query
    }

    suspend fun sort(header: DataHeader?) {
        // validate that this header is in the table
        if (header != null) {
            headers.value.firstOrNull { it.value == header.value } ?: return
        }
        // update the headers
        val updatedList = headers.value.map {
            it.copy(
                sortType = if (it == header) it.sortType.switch() else DataHeader.SortType.None
            )
        }
        headers.emit(updatedList)
        val chain = when (header?.sortType?.switch()) {
            DataHeader.SortType.Up -> "ORDER BY ${header.value} ASC"
            DataHeader.SortType.Down -> "ORDER BY ${header.value} DESC"
            DataHeader.SortType.None, null -> ""
        }
        val queryBeforeSorting = query
        query("$query $chain")
        query = queryBeforeSorting
        lastSortHeader = header
    }

    companion object {
        const val startingQuery = "SELECT * FROM ${DataRow.TableName}"
    }
}