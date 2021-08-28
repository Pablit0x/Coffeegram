package ru.beryukhov.coffeegram.pages

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.threeten.bp.LocalDate
import ru.beryukhov.coffeegram.R
import ru.beryukhov.coffeegram.data.CoffeeType
import ru.beryukhov.coffeegram.data.DayCoffee
import ru.beryukhov.coffeegram.model.DaysCoffeesState
import ru.beryukhov.coffeegram.model.DaysCoffeesStore
import ru.beryukhov.coffeegram.model.NavigationIntent
import ru.beryukhov.coffeegram.model.NavigationStore
import ru.beryukhov.coffeegram.view.CoffeeTypeItem


@Composable
fun CoffeeListAppBar(navigationStore: NavigationStore){
    TopAppBar(title = { Text(stringResource(R.string.add_drink)) },
        navigationIcon = {
            IconButton(onClick = { navigationStore.newIntent(NavigationIntent.ReturnToTablePage) }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = ""
                )
            }
        }
    )
}

@Composable
fun CoffeeListPage(daysCoffeesStore: DaysCoffeesStore, localDate: LocalDate) {
    CoffeeList(
        localDate,
        daysCoffeesStore
    )
}

@Composable
fun CoffeeList(
    localDate: LocalDate,
    daysCoffeesStore: DaysCoffeesStore,
    modifier: Modifier = Modifier
) {
    val dayCoffeeState: DaysCoffeesState by daysCoffeesStore.state.collectAsState()
    val dayCoffee = dayCoffeeState.value[localDate]?:DayCoffee()
    LazyColumn(modifier = modifier.fillMaxHeight()) {
        itemsIndexed(items = dayCoffee.coffeeCountMap.withEmpty(),
            itemContent = { _, pair: Pair<CoffeeType, Int> ->
                CoffeeTypeItem(localDate, pair.first, pair.second, daysCoffeesStore)
            })
    }
}

data class MutablePair(val ct:CoffeeType, var count:Int)

@VisibleForTesting
internal fun Map<CoffeeType, Int>.withEmpty(): List<Pair<CoffeeType, Int>> {
    val emptyList: MutableList<MutablePair> =
        CoffeeType.values().toList().map { MutablePair(it, 0) }.toMutableList()
    this.forEach { entry: Map.Entry<CoffeeType, Int> ->
        emptyList.filter{it.ct == entry.key}.forEach { it.count = entry.value }
    }
    return emptyList.map {it.ct to it.count}
}

@Preview
@Composable
private fun Preview() {
    CoffeeList(
        LocalDate.now(),
        DaysCoffeesStore(),
        modifier = Modifier
    )
}