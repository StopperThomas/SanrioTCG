package at.ac.fhstp.sanriotcg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import at.ac.fhstp.sanriotcg.data.CardDatabase
import at.ac.fhstp.sanriotcg.model.Album
import at.ac.fhstp.sanriotcg.model.Card
import at.ac.fhstp.sanriotcg.repository.AlbumRepository
import at.ac.fhstp.sanriotcg.repository.CardRepository
import at.ac.fhstp.sanriotcg.ui.theme.SanrioTCGTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var cardRepository: CardRepository
    private lateinit var albumRepository: AlbumRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = CardDatabase.getDatabase(this)
        cardRepository = CardRepository(database.cardDao())
        albumRepository = AlbumRepository(database.albumDao())

        setContent {
            SanrioTCGTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CardApp(cardRepository, albumRepository)
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
                    contentDescription = "Sanrio Logo",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "SanrioTCG",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7687D3)
                    )
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFFDBF7))
    )
}


@Composable
fun AppFooter(navController: NavHostController) {
    NavigationBar(containerColor = Color(0xFFFFDBF7)) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home", tint = Color(0xFF7687D3)) },
            label = { Text("Home", color = Color(0xFF7687D3)) },
            selected = false,
            onClick = { navController.navigate("home") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Collection", tint = Color(0xFF7687D3)) },
            label = { Text("Collection", color = Color(0xFF7687D3)) },
            selected = false,
            onClick = { navController.navigate("collection") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Album", tint = Color(0xFF7687D3)) },
            label = { Text("Album", color = Color(0xFF7687D3)) },
            selected = false,
            onClick = { navController.navigate("album") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Storefront, contentDescription = "Card Shop", tint = Color(0xFF7687D3)) },
            label = { Text("Card Shop", color = Color(0xFF7687D3)) },
            selected = false,
            onClick = { navController.navigate("shop") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Info, contentDescription = "Info", tint = Color(0xFF7687D3)) },
            label = { Text("Info", color = Color(0xFF7687D3)) },
            selected = false,
            onClick = { navController.navigate("info") }
        )
    }
}


@Composable
fun CardApp(cardRepository: CardRepository, albumRepository: AlbumRepository) {
    val navController = rememberNavController()
    val collectedCards by cardRepository.allCards.collectAsState(initial = emptyList())
    var coinBalance by remember { mutableIntStateOf(500) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = { AppHeader() },
        bottomBar = { AppFooter(navController) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            NavHost(navController = navController, startDestination = "home") {
                composable("home") { HomePage() }
                composable("collection") {
                    CollectionPage(navController, collectedCards)
                }
                composable("album") { AlbumPage(collectedCards, albumRepository) }
                composable("shop") {
                    ShopPage(
                        navController,
                        coinBalance,
                        onCoinBalanceChange = { newBalance -> coinBalance = newBalance },
                        onCardsAdded = { newCards ->
                            coroutineScope.launch {
                                newCards.forEach { card ->
                                    cardRepository.insert(card)
                                }
                            }
                        }
                    )
                }
                composable("info") { InfoPage() }
                composable("fullScreenCard/{cardId}") { backStackEntry ->
                    val cardId = backStackEntry.arguments?.getString("cardId")?.toInt() ?: 0
                    FullScreenCardPage(cardId, navController, cardRepository, onSell = {
                        coinBalance += 50
                    })
                }
                composable("packOpening/{cardIds}") { backStackEntry ->
                    val cardIdsString = backStackEntry.arguments?.getString("cardIds")
                    val cardIds = cardIdsString?.split(",")?.map { it.toInt() } ?: emptyList()
                    PackOpeningScreen(cardIds, navController)
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
            .background(color = Color(0xFFFFF0FB))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Welcome to SanrioTCG!",
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = FontFamily(
                    Font(R.font.pacifico)
                ),
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color(0xFF7687D3)
            )
        )
    }
}


@Composable
fun CollectionPage(navController: NavHostController, collectedCards: List<Card>) {
    val maxCards = 10

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .background(color = Color(0xFFFFF0FB))
    ) {
        Text(
            text = "Card Collection",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = Color(0xFF7687D3)
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

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(collectedCards) { card ->
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("fullScreenCard/${card.id}")
                        },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEEEE))
                ) {
                    Image(
                        painter = painterResource(id = card.drawableRes),
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
fun FullScreenCardPage(
    cardId: Int,
    navController: NavHostController,
    cardRepository: CardRepository,
    onSell: () -> Unit
) {
    var card by remember { mutableStateOf<Card?>(null) }

    LaunchedEffect(cardId) {
        card = cardRepository.getCardById(cardId)
    }

    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            card.let {
                if (it != null) {
                    Image(
                        painter = painterResource(id = it.drawableRes),
                        contentDescription = "Full Screen Card",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    Text(
                        text = "Sell for 50 Coins",
                        color = Color(0xFF7687D3),
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .clickable {
                                coroutineScope.launch {
                                    if (it != null) {
                                        cardRepository.delete(it)
                                    }
                                    onSell()
                                    navController.popBackStack()
                                }
                            }
                            .padding(8.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Back",
                        color = Color.Gray,
                        modifier = Modifier
                            .clickable { navController.popBackStack() }
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun AlbumPage(
    collectedCards: List<Card>,
    albumRepository: AlbumRepository
) {
    var albumName by remember { mutableStateOf("") }
    var selectedCards by remember { mutableStateOf(setOf<Card>()) }
    var isCreatingAlbum by remember { mutableStateOf(false) }
    var selectedAlbum by remember { mutableStateOf<Album?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var isEditingAlbum by remember { mutableStateOf(false) }
    val albums by albumRepository.allAlbums.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (isCreatingAlbum) {
            LaunchedEffect(isCreatingAlbum) {
                albumName = ""
                selectedCards = emptySet()
            }

            TextField(
                value = albumName,
                onValueChange = { albumName = it },
                label = { Text("Album Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Select Cards for Album", style = MaterialTheme.typography.bodyLarge)

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(collectedCards) { card ->
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                selectedCards = if (selectedCards.contains(card)) {
                                    selectedCards - card
                                } else {
                                    selectedCards + card
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedCards.contains(card)) Color(0xFF7687D3) else Color(0xFFEEEEEE)
                        )
                    ) {
                        Image(
                            painter = painterResource(id = card.drawableRes),
                            contentDescription = "Card Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val newAlbum = Album(
                        name = albumName,
                        cardIds = selectedCards.map { it.id }
                    )

                    coroutineScope.launch {
                        albumRepository.insert(newAlbum)
                    }

                    isCreatingAlbum = false
                    albumName = ""
                    selectedCards = emptySet()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = albumName.isNotEmpty() && selectedCards.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7687D3)
                )
            ) {
                Text("Create Album")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    isCreatingAlbum = false
                    albumName = ""
                    selectedCards = emptySet()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7687D3)
                )
            ) {
                Text("Cancel")
            }
        } else if (selectedAlbum != null) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = {
                        isEditingAlbum = true
                        albumName = selectedAlbum?.name ?: ""
                        selectedCards = collectedCards.filter { card ->
                            selectedAlbum?.cardIds?.contains(card.id) == true
                        }.toSet()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Album",
                        tint = Color(0xFF7687D3)
                    )
                }

                Button(
                    onClick = { selectedAlbum = null },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7687D3)
                    )
                ) {
                    Text("Back to Albums")
                }

                IconButton(
                    onClick = {
                        showDeleteConfirmation = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Album",
                        tint = Color.Red
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isEditingAlbum) {
                TextField(
                    value = albumName,
                    onValueChange = { albumName = it },
                    label = { Text("Album Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Select Cards for Album", style = MaterialTheme.typography.bodyLarge)

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(collectedCards) { card ->
                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    selectedCards = if (selectedCards.contains(card)) {
                                        selectedCards - card
                                    } else {
                                        selectedCards + card
                                    }
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedCards.contains(card)) Color(0xFF7687D3) else Color(0xFFEEEEEE)
                            )
                        ) {
                            Image(
                                painter = painterResource(id = card.drawableRes),
                                contentDescription = "Card Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        selectedAlbum?.let { album ->
                            val updatedAlbum = album.copy(name = albumName, cardIds = selectedCards.map { card -> card.id })

                            coroutineScope.launch {
                                albumRepository.update(updatedAlbum)

                                selectedAlbum = updatedAlbum
                            }
                        }
                        isEditingAlbum = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7687D3)
                    )
                ) {
                    Text("Save Changes")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        isEditingAlbum = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7687D3)
                    )
                ) {
                    Text("Cancel")
                }
            } else {
                Text(
                    text = selectedAlbum?.name ?: "",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = Color(0xFF7687D3)
                )
                Spacer(modifier = Modifier.height(16.dp))

                val cardsInAlbum = collectedCards.filter { card ->
                    selectedAlbum?.cardIds?.contains(card.id) == true
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(cardsInAlbum) { card ->
                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEEEE))
                        ) {
                            Image(
                                painter = painterResource(id = card.drawableRes),
                                contentDescription = "Card Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                            )
                        }
                    }
                }
            }
        } else {
            Text(
                text = "Your Albums",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = Color(0xFF7687D3)
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (albums.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(albums) { album ->
                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .clickable {
                                    selectedAlbum = album
                                },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEEEE))
                        ) {
                            Text(
                                text = album.name,
                                modifier = Modifier.padding(8.dp),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            )
                        }
                    }
                }
            } else {
                Text("No albums available.")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { isCreatingAlbum = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7687D3)
                )
            ) {
                Text("Start Creating Album")
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Confirm Deletion") },
            text = {
                Text("Are you sure you want to delete the album '${selectedAlbum?.name}'? This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedAlbum?.let { album ->
                            coroutineScope.launch {
                                albumRepository.delete(album)
                            }
                        }
                        selectedAlbum = null
                        showDeleteConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7687D3)
                    )
                ) {
                    Text("Yes, Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteConfirmation = false },
                    colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7687D3)
                    )
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}


@Composable
fun ShopPage(
    navController: NavHostController,
    coinBalance: Int,
    onCoinBalanceChange: (Int) -> Unit,
    onCardsAdded: (List<Card>) -> Unit
) {
    var showErrorMessage by remember { mutableStateOf(false) }

    val packPrice = 100

    val cardsWithRarity = listOf(
        Card(
            id = 1,
            drawableRes = R.drawable.cinnamoroll,
            rarity = 0.2f,
            name = "Cinnamoroll"
        ),
        Card(
            id = 2,
            drawableRes = R.drawable.pompompudding,
            rarity = 0.2f,
            name = "Pompompudding"
        ),
        Card(
            id = 3,
            drawableRes = R.drawable.ichigo_man,
            rarity = 0.1f,
            name = "Ichigo Man to the rescue!"
        ),
        Card(
            id = 4,
            drawableRes = R.drawable.tuxedo_sam,
            rarity = 0.1f,
            name = "Tuxedo Sam"
        ),
        Card(
            id = 5,
            drawableRes = R.drawable.keroppi,
            rarity = 0.1f,
            name = "Kero Kero Keroppi"
        ),
        Card(
            id = 6,
            drawableRes = R.drawable.pompompurin,
            rarity = 0.1f,
            name = "Pompompurin"
        ),
        Card(
            id = 7,
            drawableRes = R.drawable.hello_kitty,
            rarity = 0.1f,
            name = "Hello Kitty"
        ),
        Card(
            id = 8,
            drawableRes = R.drawable.cinnamon_pile,
            rarity = 0.1f,
            name = "Cinnamon Pile"
        ),
    )

    fun getRandomCard(): Card {
        val randomValue = Math.random().toFloat()
        var cumulativeProbability = 0f
        for (card in cardsWithRarity) {
            cumulativeProbability += card.rarity
            if (randomValue <= cumulativeProbability) {
                return card
            }
        }
        return cardsWithRarity.first()
    }

    fun buyPack() {
        if (coinBalance >= packPrice) {
            onCoinBalanceChange(coinBalance - packPrice)
            val newCards = List(3) { getRandomCard() }
            onCardsAdded(newCards)
            navController.navigate("packOpening/${newCards.joinToString(",") { it.id.toString() }}")
        } else {
            showErrorMessage = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFFFF0FB))
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
                color = Color(0xFF7687D3)
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
    navController: NavHostController
) {
    var currentIndex by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = getDrawableResByCardId(cardIds[currentIndex])),
                contentDescription = "Revealed Card",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (currentIndex < cardIds.size - 1) "Next" else "Finish",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF7687D3),
                modifier = Modifier
                    .clickable {
                        if (currentIndex < cardIds.size - 1) {
                            currentIndex++
                        } else {
                            navController.popBackStack()
                        }
                    }
                    .padding(8.dp)
            )
        }
    }
}


fun getDrawableResByCardId(cardId: Int): Int {
    return when (cardId) {
        1 -> R.drawable.cinnamoroll
        2 -> R.drawable.pompompudding
        3 -> R.drawable.ichigo_man
        4 -> R.drawable.tuxedo_sam
        5 -> R.drawable.keroppi
        6 -> R.drawable.pompompurin
        7 -> R.drawable.hello_kitty
        8 -> R.drawable.cinnamon_pile
        else -> R.drawable.logo
    }
}


@Composable
fun InfoPage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFFFF0FB))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Info Page",
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = FontFamily.Cursive,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color(0xFFFF69B4)
            )
        )
    }
}