package dev.logickoder.expense_manager.ui.screens.shared

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.ArrowDropUp
import androidx.compose.material.icons.outlined.Sort
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.logickoder.expense_manager.data.model.DataHeader
import dev.logickoder.expense_manager.data.model.DataRow
import dev.logickoder.expense_manager.data.model.title
import dev.logickoder.expense_manager.ui.theme.Theme
import dev.logickoder.expense_manager.ui.theme.primaryPadding
import dev.logickoder.expense_manager.ui.theme.secondaryPadding

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DataTable(
    headers: List<DataHeader>,
    items: List<DataRow>,
    modifier: Modifier = Modifier,
    itemWidth: Dp = 150.dp,
    onHeaderClick: (DataHeader) -> Unit,
    onRowClick: (DataRow) -> Unit,
) {
    val padding = primaryPadding() / 2
    val tableWidth = remember {
        items.firstOrNull()?.let {
            itemWidth * (it.items.size + 1) + (padding * 2)
        } ?: 0.dp
    }
    LazyColumn(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        content = {
            stickyHeader {
                DataRow(
                    modifier = Modifier
                        .width(tableWidth)
                        .background(Color.White)
                        .padding(horizontal = padding),
                    items = headers,
                    itemCreator = { _, header ->
                        DataHeader(
                            modifier = Modifier
                                .clickable { onHeaderClick(header) }
                                .width(itemWidth)
                                .padding(vertical = padding * 2),
                            text = header.title,
                            type = header.sortType,
                        )
                    },
                )
            }
            items(
                count = items.size,
                key = { i -> items[i].id },
                itemContent = { i ->
                    val row = items[i]
                    Divider(
                        thickness = 2.dp,
                        modifier = Modifier.width(tableWidth),
                    )
                    DataRow(
                        modifier = Modifier
                            .clickable {
                                onRowClick(row)
                            }
                            .padding(padding),
                        items = row.items,
                        itemCreator = { index, text ->
                            DataItem(
                                modifier = Modifier.width(
                                    if (index == row.items.lastIndex) {
                                        itemWidth * 2
                                    } else itemWidth
                                ),
                                text = text,
                            )
                        }
                    )
                }
            )
        }
    )
}

@Composable
fun <T> DataRow(
    modifier: Modifier = Modifier,
    items: List<T>,
    itemCreator: @Composable (Int, T) -> Unit,
) {
    Row(
        modifier = modifier,
        content = {
            items.forEachIndexed { index, item ->
                itemCreator(index, item)
            }
        },
    )
}

@Composable
fun DataItem(
    modifier: Modifier = Modifier,
    text: String,
) {
    Text(
        modifier = modifier,
        text = text,
        maxLines = 1,
    )
}

@Composable
fun DataHeader(
    modifier: Modifier = Modifier,
    text: String,
    type: DataHeader.SortType,
) {
    val color =
        if (type == DataHeader.SortType.None) Color.LightGray else Theme.colors.primary
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(secondaryPadding() / 4),
        verticalAlignment = Alignment.CenterVertically,
        content = {
            Text(
                text = text,
                style = Theme.typography.body1.copy(fontWeight = FontWeight.Bold),
                color = if (type == DataHeader.SortType.None) Color.Black else Theme.colors.primary,
            )
            Icon(
                imageVector = when (type) {
                    DataHeader.SortType.Up -> Icons.Outlined.ArrowDropUp
                    DataHeader.SortType.Down -> Icons.Outlined.ArrowDropDown
                    DataHeader.SortType.None -> Icons.Outlined.Sort
                },
                tint = color,
                contentDescription = null
            )
        }
    )
}