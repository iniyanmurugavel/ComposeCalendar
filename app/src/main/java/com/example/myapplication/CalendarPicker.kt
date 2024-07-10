package com.example.myapplication

import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.DateFormatSymbols
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Calendar
import java.util.Locale

private const val MAX_SPOTS_FOR_DAYS_IN_MONTH = 42
private const val DAYS_OF_WEEK = 7

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyCalendar(
    modifier: Modifier = Modifier,
    today: LocalDate = LocalDate.now(),
    ui: CalendarUI,
    selected: LocalDate,
    onClick: (LocalDate) -> Unit = {},
) {
    val calendarSessions = getCalendarSessions(ui.availableSessions)
    val keys = calendarSessions.keys
    val pagerState = rememberPagerState(pageCount = { calendarSessions.size })
    val coroutineScope = rememberCoroutineScope()
    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp, start = 8.dp, end = 8.dp),
        ) {
            HorizontalPager(state = pagerState) { page ->
                Column(
                    modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MonthRow(
                        month = keys.elementAt(page).first,
                        year = keys.elementAt(page).second,
                        goLeft = {
                            handleScrolling(coroutineScope = coroutineScope,
                                scrollCheck = { pagerState.currentPage == 0 },
                                scrollAction = { pagerState.animateScrollToPage(pagerState.currentPage - 1) })
                        },
                        goRight = {
                            handleScrolling(coroutineScope = coroutineScope,
                                scrollCheck = { pagerState.currentPage == calendarSessions.size - 1 },
                                scrollAction = { pagerState.animateScrollToPage(pagerState.currentPage + 1) })
                        },
                        isFirstMonth = page == 0,
                        isLastMonth = pagerState.pageCount == page + 1
                    )
                    WeekDayRow()

                    calendarSessions[keys.elementAt(page)]?.let { monthSessions ->
                        if (monthSessions.isNotEmpty()) {
                            DayPicker(
                                availableSessions = monthSessions,
                                today = today,
                                selected = selected,
                                onClick = onClick
                            )
                        } else {
                            EmptyMonth(
                                today = today,
                                monthValue = keys.elementAt(page).first,
                                year = keys.elementAt(page).second
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun handleScrolling(
    coroutineScope: CoroutineScope, scrollCheck: () -> Boolean, scrollAction: suspend () -> Unit
) = coroutineScope.launch {
    if (scrollCheck()) {
        return@launch
    } else {
        scrollAction()
    }
}

@Composable
private fun MonthRow(
    month: Int,
    year: Int,
    locale: Locale = Locale.US,
    goLeft: () -> Unit,
    goRight: () -> Unit,
    isFirstMonth: Boolean,
    isLastMonth: Boolean
) {
    val monthName = Month.of(month).getDisplayName(TextStyle.FULL, locale)
    Row {
        Icon(
            modifier = Modifier
                .padding(4.dp)
                .clickable(enabled = !isFirstMonth, onClick = goLeft),
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "",
            tint = if (isFirstMonth) Color(0xFFC1BDC7) else Color(
                0xFF3659E3
            )
        )
        Text(
            modifier = Modifier.weight(1f),
            text = "$monthName $year",
            textAlign = TextAlign.Center,
            color = Color(0xFF1E0A3C),
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 20.sp
            )
        )
        Icon(
            modifier = Modifier
                .padding(4.dp)
                .clickable(enabled = !isLastMonth, onClick = goRight),
            imageVector = Icons.Filled.ArrowForward,
            contentDescription = "",
            tint = if (isLastMonth) Color(0xFFC1BDC7)
            else Color(
                0xFF3659E3
            )
        )
    }
}

@Composable
private fun WeekDayRow(locale: Locale = Locale.US) {
    val weekDays = DateFormatSymbols.getInstance(locale).shortWeekdays
    val firstDay = Calendar.getInstance(locale).firstDayOfWeek
    val daysOfWeek = List(DAYS_OF_WEEK) { count -> weekDays[(firstDay + count) % 9] }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        daysOfWeek.forEach { weekDay ->
            Text(
                text = weekDay.take(2).capitalize(locale),
                color = Color(0xFF1E0A3C),
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 16.sp
                )
            )
        }
    }
}

@Composable
private fun DayPicker(
    availableSessions: List<LocalDate>,
    today: LocalDate,
    selected: LocalDate,
    onClick: (LocalDate) -> Unit,
) {

    val monthData = calendarLogic(
        month = availableSessions.first().monthValue, year = availableSessions.first().year
    )

    LazyColumn {
        items(count = monthData.size) { indexWeek ->
            LazyRow(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                items(count = monthData[indexWeek].size) { indexDay ->
                    Day(isNotThisMonth = monthData[indexWeek][indexDay].monthValue != availableSessions.first().monthValue,
                        isSelected = monthData[indexWeek][indexDay] == selected,
                        isCurrentDay = monthData[indexWeek][indexDay] == LocalDate.of(
                            today.year, today.month.value, today.dayOfMonth
                        ),
                        hasSessions = availableSessions.contains(monthData[indexWeek][indexDay]),
                        day = monthData[indexWeek][indexDay].dayOfMonth,
                        onDayClicked = { onClick.invoke(monthData[indexWeek][indexDay]) })
                }
            }
        }
    }
}

@Composable
private fun EmptyMonth(
    today: LocalDate, monthValue: Int, year: Int
) {

    val monthData = calendarLogic(
        month = monthValue, year = year
    )

    LazyColumn {
        items(count = monthData.size) { indexWeek ->
            LazyRow(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                items(count = monthData[indexWeek].size) { indexDay ->
                    Day(
                        isNotThisMonth = monthData[indexWeek][indexDay].monthValue != monthValue,
                        isSelected = false,
                        isCurrentDay = monthData[indexWeek][indexDay] == LocalDate.of(
                            today.year, today.month.value, today.dayOfMonth
                        ),
                        hasSessions = false,
                        day = monthData[indexWeek][indexDay].dayOfMonth,
                    )
                }
            }
        }
    }
}

private fun calendarLogic(
    month: Int, year: Int, locale: Locale = Locale.US
): List<List<LocalDate>> {
    val date = LocalDate.of(year, month, 1)
    val dayOfWeek = date.dayOfWeek
    val firstDayOfWeek: DayOfWeek = WeekFields.of(locale).firstDayOfWeek

    return List(MAX_SPOTS_FOR_DAYS_IN_MONTH) { position ->
        val daysOfDifference = firstDayOfWeek.ordinal - dayOfWeek.ordinal/* We need to take account that some locales start in Sunday, so in case of Monday-started week
         *
         *  | Mon | Tue | Wed | Thu | Fri | Sat | Sun |
         *  |:---:|:---:|:---:|:---:|:---:|:---:|:---:|
         *  |  0  |  1  |  2  |  3  |  4  |  5  |  6  |
         *
         * So Mon (0)  - Thu (3) = -3.
         *
         * But if we are in a Sunday-based week:
         * | Sun | Mon | Tue | Wed | Thu | Fri | Sat |
         * |:---:|:---:|:---:|:---:|:---:|:---:|:---:|
         * |  6  |  0  |  1  |  2  |  3  |  4  |  5  |
         *
         * Sun (6) - Thu (3) = 3.
         *
         * In this case, we have a positive result due to the ordinals, so we need to displace "an additional" week
         * to proper perform the calculations:
         *
         * | Thu | Fri | Sat | Sun | Mon | Tue | Wed | Thu | Fri | Sat |
         * |:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
         * |  3  |  4  |  5  |  6  |  0  |  1  |  2  |  3  |  4  |  5  |
         *
         * So Sun (6) - Thu (3) - Displacement (7) = -4
         *
         * Which is the difference between a SUN and its next THU
         *
         */
        val adjustment = if (daysOfDifference > 0) DayOfWeek.entries.size else 0

        val adjustedDate = date.plusDays((daysOfDifference - adjustment + position).toLong())
        LocalDate.of(adjustedDate.year, adjustedDate.month.value, adjustedDate.dayOfMonth)
    }.chunked(DAYS_OF_WEEK)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun getCalendarSessions(availableSessions: List<LocalDate>): Map<Pair<Int, Int>, List<LocalDate>> {
    val minDate = availableSessions.minOrNull()
    val maxDate = availableSessions.maxOrNull()

    val allMonthsYears = minDate?.let { startDate ->
        maxDate?.let { endDate ->
            generateSequence(startDate) {
                it.withDayOfMonth(1).plusMonths(1)
            }.takeWhile { it <= endDate }.map { Pair(it.monthValue, it.year) }.toList()
        }
    } ?: emptyList()

    val groupedSessions = allMonthsYears.associateWith { monthYear ->
        availableSessions.filter { it.monthValue == monthYear.first && it.year == monthYear.second }
    }

    return groupedSessions
}

internal class CalendarPreview {
    @Preview(name = "Light Mode")
    @Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Composable
    private fun CalendarComponent() {
        MyApplicationTheme() {
            MyCalendar(
                today = LocalDate.of(2024, 2, 17),
                selected = LocalDate.of(2024, 2, 17),
                ui = CalendarUI(
                    availableSessions = listOf(
                        LocalDate.of(2024, 2, 17),
                        LocalDate.of(2024, 2, 21),
                        LocalDate.of(2024, 2, 28),
                        LocalDate.of(2024, 6, 17),
                        LocalDate.of(2024, 6, 21),
                        LocalDate.of(2024, 6, 28),
                        LocalDate.of(2025, 1, 17),
                        LocalDate.of(2025, 1, 21),
                        LocalDate.of(2025, 1, 28)
                    )
                )
            )
        }
    }
}