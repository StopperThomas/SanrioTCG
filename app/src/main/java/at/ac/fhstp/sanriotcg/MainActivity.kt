package at.ac.fhstp.sanriotcg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import at.ac.fhstp.sanriotcg.ui.theme.SanrioTCGTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SanrioTCGTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CardApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppHeader() {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("SanrioTCG", style = MaterialTheme.typography.titleLarge, color = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1976D2))
    )
}

@Composable
fun AppFooter(navController: NavHostController) {
    NavigationBar(containerColor = Color(0xFF1976D2)) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.White) },
            label = { Text("Home", color = Color.White) },
            selected = false,
            onClick = { navController.navigate("home") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Collection", tint = Color.White) },
            label = { Text("Collection", color = Color.White) },
            selected = false,
            onClick = { navController.navigate("collection") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Album", tint = Color.White) },
            label = { Text("Album", color = Color.White) },
            selected = false,
            onClick = { navController.navigate("album") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Storefront, contentDescription = "Card Shop", tint = Color.White) },
            label = { Text("Card Shop", color = Color.White) },
            selected = false,
            onClick = { navController.navigate("shop") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Info, contentDescription = "Info", tint = Color.White) },
            label = { Text("Info", color = Color.White) },
            selected = false,
            onClick = { navController.navigate("info") }
        )
    }
}

@Composable
fun CardApp() {
    val navController = rememberNavController()

    // Move the coin balance state here
    var coinBalance by remember { mutableStateOf(500) }

    // State for collected cards (using a Set to avoid duplicates)
    var collectedCards by remember { mutableStateOf(setOf<Int>()) }

    Scaffold(
        topBar = { AppHeader() },
        bottomBar = { AppFooter(navController) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            NavHost(navController = navController, startDestination = "home") {
                composable("home") { HomePage() }
                composable("collection") { CollectionPage(navController, collectedCards) }
                composable("album") { AlbumPage() }
                composable("shop") {
                    // Pass coinBalance and a function to update it to the ShopPage
                    ShopPage(navController, coinBalance, onCoinBalanceChange = { newBalance ->
                        coinBalance = newBalance
                    })
                }
                composable("info") { InfoPage() }
                composable("fullScreenCard/{cardId}") { backStackEntry ->
                    val cardId = backStackEntry.arguments?.getString("cardId")?.toInt() ?: R.drawable.cinnamoroll
                    FullScreenCardPage(cardId, navController)
                }
                composable("packOpening/{cardIds}") { backStackEntry ->
                    val cardIdsString = backStackEntry.arguments?.getString("cardIds")
                    val cardIds = cardIdsString?.split(",")?.map { it.toInt() } ?: emptyList()
                    PackOpeningScreen(cardIds, navController, onCardsRevealed = { newCards ->
                        collectedCards = collectedCards + newCards // Add new cards to the set
                    })
                }
            }
        }
    }
}



@Composable
fun HomePage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Home Page",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = Color(0xFF1976D2)
        )
    }
}

@Composable
fun CollectionPage(navController: NavHostController, collectedCards: Set<Int>) {
    val maxCards = 10

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Card Collection",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = Color(0xFF1976D2)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Collected Cards: ${collectedCards.size}/$maxCards",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            ),
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Scrollable grid of collected cards
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(collectedCards.toList()) { cardId -> // Convert the set to a list
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .clickable {
                            // Navigate to full-screen card page
                            navController.navigate("fullScreenCard/$cardId")
                        },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEEEE))
                ) {
                    Image(
                        painter = painterResource(id = cardId),
                        contentDescription = "Card Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    )
                }
            }
        }
    }
}





@Composable
fun FullScreenCardPage(cardId: Int, navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { navController.popBackStack() }, // Close on tap outside the image
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = cardId),
            contentDescription = "Full Screen Card",
            modifier = Modifier.fillMaxSize()
        )
    }
}


@Composable
fun AlbumPage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Album Page",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = Color(0xFF1976D2)
        )
    }
}

@Composable
fun ShopPage(
    navController: NavHostController,
    coinBalance: Int,
    onCoinBalanceChange: (Int) -> Unit // Callback to update the coin balance
) {
    var showErrorMessage by remember { mutableStateOf(false) }

    val packPrice = 100

    val cardsWithRarity = listOf(
        at.ac.fhstp.sanriotcg.model.Card(
            drawableRes = R.drawable.cinnamoroll,
            rarity = 0.5f,
            name = "Cinnamoroll"
        ),
        at.ac.fhstp.sanriotcg.model.Card(
            drawableRes = R.drawable.pompompudding,
            rarity = 0.5f,
            name = "Pompompudding"
        ),
    )

    fun getRandomCard(): Int {
        val randomValue = Math.random().toFloat()
        var cumulativeProbability = 0f
        for (card in cardsWithRarity) {
            cumulativeProbability += card.rarity
            if (randomValue <= cumulativeProbability) {
                return card.drawableRes
            }
        }
        return cardsWithRarity.first().drawableRes
    }

    fun buyPack() {
        if (coinBalance >= packPrice) {
            onCoinBalanceChange(coinBalance - packPrice) // Update the coin balance
            val newCards = List(3) { getRandomCard() }
            navController.navigate("packOpening/${newCards.joinToString(",")}")
            showErrorMessage = false
        } else {
            showErrorMessage = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Coins: $coinBalance",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = Color(0xFF1976D2)
            )
            Spacer(modifier = Modifier.height(32.dp))

            Image(
                painter = painterResource(id = R.drawable.card_pack),
                contentDescription = "Card Pack",
                modifier = Modifier
                    .size(150.dp)
                    .clickable { buyPack() }
            )

            if (showErrorMessage) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "You don't have enough coins!",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun PackOpeningScreen(
    cardIds: List<Int>,
    navController: NavHostController,
    onCardsRevealed: (List<Int>) -> Unit // Callback to add cards to collection
) {
    // State to track the current card index
    var currentIndex by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Display the current card image
            Image(
                painter = painterResource(id = cardIds[currentIndex]),
                contentDescription = "Revealed Card",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Display "Next" or "Finish" button
            Text(
                text = if (currentIndex < cardIds.size - 1) "Next" else "Finish",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF1976D2),
                modifier = Modifier
                    .clickable {
                        if (currentIndex < cardIds.size - 1) {
                            // Move to the next card
                            currentIndex++
                        } else {
                            // Add cards to the collection and navigate back to the shop
                            onCardsRevealed(cardIds)
                            navController.popBackStack()
                        }
                    }
                    .padding(8.dp) // Add padding for better clickability
            )
        }
    }
}




@Composable
fun InfoPage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Info Page",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = Color(0xFF1976D2)
        )
    }
}
