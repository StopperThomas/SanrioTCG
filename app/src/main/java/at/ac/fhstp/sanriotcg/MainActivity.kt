package at.ac.fhstp.sanriotcg

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import at.ac.fhstp.sanriotcg.data.CardDatabase
import at.ac.fhstp.sanriotcg.model.Album
import at.ac.fhstp.sanriotcg.model.Card
import at.ac.fhstp.sanriotcg.model.Challenge
import at.ac.fhstp.sanriotcg.model.Circle
import at.ac.fhstp.sanriotcg.repository.AlbumRepository
import at.ac.fhstp.sanriotcg.repository.CardRepository
import at.ac.fhstp.sanriotcg.ui.theme.SanrioTCGTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    private lateinit var cardRepository: CardRepository
    private lateinit var albumRepository: AlbumRepository

    private val challenges = mutableListOf(
        Challenge("Open 10 Packs", 10, 0, 500),
        Challenge("Collect 5 Rare Cards", 5, 0, 300),
        Challenge("Spend 500 Coins", 500, 0, 200),
        Challenge("Sell 5 Cards", 5, 0, 150)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = CardDatabase.getDatabase(this)
        cardRepository = CardRepository(database.cardDao())
        albumRepository = AlbumRepository(database.albumDao())

        setContent {
            SanrioTCGTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CardApp(
                        cardRepository,
                        albumRepository,
                        challenges
                    )
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


@SuppressLint("MutableCollectionMutableState")
@Composable
fun CardApp(
    cardRepository: CardRepository,
    albumRepository: AlbumRepository,
    challenges: MutableList<Challenge>
) {
    val navController = rememberNavController()
    val collectedCards by cardRepository.allCards.collectAsState(initial = emptyList())
    var coinBalance by remember { mutableIntStateOf(500) }
    var cardsSold by remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    val challengesState = remember { mutableStateOf(challenges) }

    val totalSpent = remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = { AppHeader() },
        bottomBar = { AppFooter(navController) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            NavHost(navController = navController, startDestination = "home") {
                composable("home") {
                    HomePage(
                        challenges = challengesState.value,
                        coinBalance = coinBalance,
                        onCoinBalanceChange = { newBalance -> coinBalance = newBalance }
                    )
                }
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
                        },
                        challenges = challengesState,
                        totalSpent = totalSpent
                    )
                }
                composable("info") { InfoPage() }
                composable("fullScreenCard/{cardId}") { backStackEntry ->
                    val cardId = backStackEntry.arguments?.getString("cardId")?.toInt() ?: 0
                    FullScreenCardPage(cardId, navController, cardRepository, onSell = {
                        coinBalance += 50
                        cardsSold++
                        val sellChallenge = challengesState.value.find { it.name == "Sell 5 Cards" }
                        sellChallenge?.let {
                            it.progress = minOf(cardsSold, it.target)
                        }
                    })
                }
                composable("packOpening/{cardIds}") { backStackEntry ->
                    val cardIdsString = backStackEntry.arguments?.getString("cardIds")
                    val cardIds = cardIdsString?.split(",")?.map { it.toInt() } ?: emptyList()
                    PackOpeningScreen(cardIds, navController)
                }
                composable("minigame") {
                    MinigamePage(
                        navController = navController,
                        onCoinBalanceChange = { earnedCoins ->
                            coinBalance += earnedCoins
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun HomePage(
    challenges: MutableList<Challenge>,
    coinBalance: Int,
    onCoinBalanceChange: (Int) -> Unit
) {
    var showClaimDialog by remember { mutableStateOf(false) }

    fun calculateRewardCoins(): Int {
        return challenges.filter { it.progress >= it.target && !it.claimed }
            .sumOf { it.reward }
    }

    var rewardCoins by remember { mutableIntStateOf(0) }

    val hasUnclaimedRewards = challenges.any { it.progress >= it.target && !it.claimed }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFFFF0FB))
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Welcome to SanrioTCG!",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily(Font(R.font.pacifico)),
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color(0xFF7687D3)
                )
            )
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Your Current Challenges:",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = Color(0xFF7687D3)
            )
            Spacer(modifier = Modifier.height(16.dp))

            challenges.forEach { challenge ->
                ChallengeItem(challenge)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    rewardCoins = calculateRewardCoins()
                    showClaimDialog = true
                },
                enabled = hasUnclaimedRewards,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7687D3))
            ) {
                Text("Claim Rewards", color = Color.White, fontFamily = FontFamily.SansSerif)
            }

            if (showClaimDialog) {
                AlertDialog(
                    onDismissRequest = { showClaimDialog = false },
                    title = { Text("Claim Rewards", fontFamily = FontFamily.SansSerif) },
                    text = { Text("You have earned $rewardCoins coins.", fontFamily = FontFamily.SansSerif) },
                    confirmButton = {
                        Button(
                            onClick = {
                                onCoinBalanceChange(coinBalance + rewardCoins)

                                challenges.filter { it.progress >= it.target && !it.claimed }
                                    .forEach { it.claimed = true }

                                rewardCoins = 0
                                showClaimDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7687D3))
                        ) {
                            Text("Claim", color = Color.White, fontFamily = FontFamily.SansSerif)
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showClaimDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7687D3))
                        ) {
                            Text("Cancel", color = Color.White, fontFamily = FontFamily.SansSerif)
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun ChallengeItem(challenge: Challenge) {
    val progress = remember { mutableIntStateOf(challenge.progress) }

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Text(
            text = challenge.name,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            color = Color(0xFF7687D3)
        )
        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { progress.intValue / challenge.target.toFloat() },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = Color(0xFF7687D3),
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "${progress.intValue} / ${challenge.target} - Reward: ${challenge.reward} coins",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
            color = Color.Gray
        )
    }
}


@Composable
fun CollectionPage(navController: NavHostController, collectedCards: List<Card>) {
    val maxCards = 10

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFFFF0FB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
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
                contentPadding = PaddingValues(0.dp)
            ) {
                items(collectedCards) { card ->
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("fullScreenCard/${card.id}")
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFDBF7).copy(alpha = 0.5f))
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
                        color = Color(0xFFFF6961),
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
                        color = Color(0xFF7687D3),
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .clickable { navController.popBackStack() }
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}


@Suppress("DEPRECATION")
@OptIn(ExperimentalMaterial3Api::class)
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFFFF0FB))
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            if (isCreatingAlbum) {
                LaunchedEffect(isCreatingAlbum) {
                    albumName = ""
                    selectedCards = emptySet()
                }

                TextField(
                    value = albumName,
                    onValueChange = { albumName = it },
                    label = {
                        Text(
                            "Album Name",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color(0xFF7687D3),
                                fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(
                            Color(0xFFE8EAF6),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    colors = TextFieldDefaults.textFieldColors(
                        focusedLabelColor = Color(0xFF7687D3),
                        unfocusedLabelColor = Color(0xFF7687D3),
                        focusedIndicatorColor = Color(0xFF7687D3),
                        unfocusedIndicatorColor = Color(0xFFB0BEC5),
                        containerColor = Color.Transparent,
                        focusedTextColor = Color(0xFF7687D3),
                        unfocusedTextColor = Color(0xFF7687D3),
                        cursorColor = Color(0xFF7687D3)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Select Cards for Album", style = MaterialTheme.typography.bodyLarge)

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(0.dp)
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
                                containerColor = if (selectedCards.contains(card)) Color(0xFF7687D3)
                                else Color(0xFFFFDBF7).copy(alpha = 0.5f)
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
                            tint = Color(0xFFFF6961)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isEditingAlbum) {
                    TextField(
                        value = albumName,
                        onValueChange = { albumName = it },
                        label = {
                            Text(
                                "Album Name",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color(0xFF7687D3),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(
                                Color(0xFFE8EAF6),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        colors = TextFieldDefaults.textFieldColors(
                            focusedLabelColor = Color(0xFF7687D3),
                            unfocusedLabelColor = Color(0xFF7687D3),
                            focusedIndicatorColor = Color(0xFF7687D3),
                            unfocusedIndicatorColor = Color(0xFFB0BEC5),
                            containerColor = Color.Transparent,
                            focusedTextColor = Color(0xFF7687D3),
                            unfocusedTextColor = Color(0xFF7687D3),
                            cursorColor = Color(0xFF7687D3)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Select Cards for Album", style = MaterialTheme.typography.bodyLarge)

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(0.dp)
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
                                    containerColor = if (selectedCards.contains(card)) Color(
                                        0xFF7687D3
                                    ) else Color(0xFFFFDBF7).copy(alpha = 0.5f)
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
                                val updatedAlbum = album.copy(
                                    name = albumName,
                                    cardIds = selectedCards.map { card -> card.id })

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
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        items(cardsInAlbum) { card ->
                            Card(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFDBF7).copy(alpha = 0.5f))
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
                        columns = GridCells.Fixed(1),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        items(albums) { album ->
                            val albumCards = collectedCards.filter { card -> album.cardIds.contains(card.id) }
                            val thumbnailCard = albumCards.firstOrNull()

                            Card(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedAlbum = album
                                    }
                                    .border(2.dp, Color(0xFF7687D3), RoundedCornerShape(12.dp)),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFDBF7).copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = album.name,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "${albumCards.size} Cards",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF7687D3)
                                        )
                                    }

                                    if (thumbnailCard != null) {
                                        Image(
                                            painter = painterResource(id = thumbnailCard.drawableRes),
                                            contentDescription = "Thumbnail",
                                            modifier = Modifier
                                                .size(120.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                        )
                                    }
                                }
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
    onCardsAdded: (List<Card>) -> Unit,
    challenges: MutableState<MutableList<Challenge>>,
    totalSpent: MutableState<Int>
) {
    var showErrorMessage by remember { mutableStateOf(false) }

    val packPrice = 100

    val cardsWithRarity = listOf(
        Card(id = 1, drawableRes = R.drawable.cinnamoroll, rarity = 0.2f, name = "Cinnamoroll"), // Common
        Card(id = 2, drawableRes = R.drawable.pompompudding, rarity = 0.15f, name = "Pompompudding"), // Spell - Common
        Card(id = 3, drawableRes = R.drawable.ichigo_man, rarity = 0.1f, name = "Ichigo Man to the rescue!"), // Trap - Common
        Card(id = 4, drawableRes = R.drawable.tuxedo_sam, rarity = 0.1f, name = "Tuxedo Sam"), // Uncommon
        Card(id = 5, drawableRes = R.drawable.keroppi, rarity = 0.1f, name = "Kero Kero Keroppi"), // Uncommon
        Card(id = 6, drawableRes = R.drawable.pompompurin, rarity = 0.1f, name = "Pompompurin"), // Uncommon
        Card(id = 7, drawableRes = R.drawable.hello_kitty, rarity = 0.1f, name = "Hello Kitty"), // Uncommon
        Card(id = 8, drawableRes = R.drawable.cinnamon_pile, rarity = 0.1f, name = "Cinnamon Pile"), // Spell - Common
        Card(id = 9, drawableRes = R.drawable.my_melody, rarity = 0.05f, name = "My Melody"), // Uncommon
        Card(id = 10, drawableRes = R.drawable.best_friends_forever, rarity = 0.05f, name = "Best Friends Forever"), // Rare
        Card(id = 11, drawableRes = R.drawable.shadow, rarity = 0.025f, name = "Shadow") // Legendary
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

    fun buyPack(
        challenges: MutableState<MutableList<Challenge>>,
        coinBalance: Int,
        onCoinBalanceChange: (Int) -> Unit,
        onCardsAdded: (List<Card>) -> Unit,
        navController: NavHostController,
        totalSpent: MutableState<Int>
    ) {
        if (coinBalance >= packPrice) {
            onCoinBalanceChange(coinBalance - packPrice)

            val newCards = List(3) { getRandomCard() }
            onCardsAdded(newCards)

            totalSpent.value += packPrice

            val openPacksChallenge = challenges.value.find { it.name == "Open 10 Packs" }
            openPacksChallenge?.let {
                it.progress = minOf(it.progress + 1, it.target)
            }

            newCards.forEach { card ->
                if (card.rarity < 0.1) {
                    val rareCardChallenge = challenges.value.find { it.name == "Collect 5 Rare Cards" }
                    rareCardChallenge?.let {
                        it.progress = minOf(it.progress + 1, it.target)
                    }
                }
            }

            val spendChallenge = challenges.value.find { it.name == "Spend 500 Coins" }
            spendChallenge?.let {
                it.progress = minOf(totalSpent.value, it.target)
            }

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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Coins: $coinBalance",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = Color(0xFF7687D3)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.card_pack),
                    contentDescription = "Card Pack",
                    modifier = Modifier
                        .clickable {
                            buyPack(challenges, coinBalance, onCoinBalanceChange, onCardsAdded, navController, totalSpent)
                        }
                        .size(275.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Price: $packPrice Coins",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = Color(0xFF7687D3)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (showErrorMessage) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Out of money? Play a game!",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { navController.navigate("minigame") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7687D3))) {
                        Text(text = "Play Minigame"
                        )
                    }
                }
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
    var isAnimating by remember { mutableStateOf(false) }

    LaunchedEffect(isAnimating) {
        if (isAnimating) {
            delay(300)
            currentIndex++
            isAnimating = false
        }
    }

    val transition = updateTransition(targetState = isAnimating, label = "Card Transition")

    val scale by transition.animateFloat(
        transitionSpec = {
            tween(durationMillis = 300, easing = FastOutSlowInEasing)
        },
        label = "Scale Animation"
    ) { animating -> if (animating) 0.5f else 1f }

    val alpha by transition.animateFloat(
        transitionSpec = {
            tween(durationMillis = 300, easing = LinearEasing)
        },
        label = "Alpha Animation"
    ) { animating -> if (animating) 0f else 1f }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(500.dp)
                    .aspectRatio(1f)
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        alpha = alpha
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = getDrawableResByCardId(cardIds[currentIndex])),
                    contentDescription = "Revealed Card",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (currentIndex < cardIds.size - 1) "Next" else "Finish",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF7687D3),
                modifier = Modifier
                    .clickable {
                        if (currentIndex < cardIds.size - 1) {
                            if (!isAnimating) {
                                isAnimating = true
                            }
                        } else {
                            navController.popBackStack()
                        }
                    }
                    .padding(8.dp)
            )
        }
    }
}


@Composable
fun MinigamePage(
    navController: NavHostController,
    onCoinBalanceChange: (Int) -> Unit
) {
    var earnedCoins by remember { mutableIntStateOf(0) }
    var circles by remember { mutableStateOf(listOf<Circle>()) }
    var showDialog by remember { mutableStateOf(false) }
    var isSpawningCircles by remember { mutableStateOf(true) }  // Flag to control circle spawning
    val coroutineScope = rememberCoroutineScope()
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val paddingTop = 0.dp
    val paddingBottom = 300.dp

    fun generateRandomCircle(): Circle {
        val id = Random.nextInt()
        val x = Random.nextFloat() * 0.8f + 0.1f
        val y = Random.nextFloat() * 0.8f + 0.1f
        return Circle(id, x, y)
    }

    fun removeCircleAfterDelay(circle: Circle, delayMillis: Long) {
        coroutineScope.launch {
            delay(delayMillis)
            circles = circles.filter { it.id != circle.id }
        }
    }

    LaunchedEffect(isSpawningCircles) {
        while (isSpawningCircles) {
            val newCircle = generateRandomCircle()
            circles = circles + newCircle
            removeCircleAfterDelay(newCircle, 1000L)
            delay(1000L)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF0FB))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingTop, bottom = paddingBottom) // Padding applied here
        ) {
            circles.forEach { circle ->
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .offset(
                            x = (circle.x * screenWidth.value).dp,
                            y = (circle.y * (screenHeight.value - paddingTop.value - paddingBottom.value)).dp + paddingTop // Correct calculation for y
                        )
                        .clip(CircleShape)
                        .clickable {
                            earnedCoins += 10
                            circles = circles.filter { it.id != circle.id }
                        }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.coin),
                        contentDescription = "Coin Circle",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Coins Earned: $earnedCoins",
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    color = Color(0xFF7687D3),
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            )
        }

        // Return to Shop Button
        Button(
            onClick = {
                isSpawningCircles = false  // Stop spawning circles when button is pressed
                showDialog = true
            },
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7687D3))
        ) {
            Text(
                text = "Return to Shop",
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFF0FB),
                    fontSize = 18.sp
                )
            )
        }

        // Show Dialog with individual styling for text and button
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Text(
                        text = "Good Job!",
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFF7687D3)
                        )
                    )
                },
                text = {
                    Text(
                        text = "You earned $earnedCoins coins!",
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = Color(0xFF7687D3)
                        )
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onCoinBalanceChange(earnedCoins)
                            earnedCoins = 0
                            showDialog = false
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7687D3))
                    ) {
                        Text(
                            text = "OK",
                            style = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFFFFF0FB)
                            )
                        )
                    }
                }
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
        9 -> R.drawable.my_melody
        10 -> R.drawable.best_friends_forever
        11 -> R.drawable.shadow
        else -> R.drawable.logo
    }
}


@Composable
fun InfoPage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFFFF0FB))
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Info / Help",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = Color(0xFF7687D3)
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            InfoSection(
                title = "How Challenges Work",
                content = """
                    Challenges are tasks you can complete to earn coins and rewards. 
                    Some challenges may require you to open card packs, collect rare cards, or sell cards. 
                    Completing challenges will reward you with coins, which can be used to buy more card packs!
                """.trimIndent()
            )

            InfoSection(
                title = "Collecting Cards",
                content = """
                    You can collect cards by purchasing and opening card packs in the Card Shop. 
                    Each pack contains 3 random cards with varying rarities. 
                    
                    Cards are categorized into common, uncommon, rare and legendary types.
                    Rarity is determined by the amount of stars.
                    Spell and Trap cards don't have a star value, but are treated like Common cards.
                    
                    Common cards have either 1, 2 or 3 stars.
                    Uncommon cards have 4 or 5 stars.
                    Rare cards have 6 or 7 stars.
                    And Legendary cards have 8 stars.
                """.trimIndent()
            )

            InfoSection(
                title = "How to Earn Coins",
                content = """
                    You can earn coins in several ways:
                    - Completing challenges
                    - Selling unwanted cards
                    - Play Minigame
                    
                    Use your coins to buy more card packs and continue expanding your collection!
                """.trimIndent()
            )

            InfoSection(
                title = "How the Minigame Works",
                content = """
                    The Minigame, which can only be played when you're out of coins, allows you to earn coins by clicking on randomly spawning coin circles.
                    - Coins spawn at random positions on the screen.
                    - Each time you click on a coin, you earn 10 coins.
                    - The coins will disappear after 1 second, so be quick to collect them!
                    - You can stop the minigame at any time by clicking the 'Return to Shop' button.
                """.trimIndent()
            )

            InfoSection(
                title = "Creating Albums",
                content = """
                    In the Albums Section, you can create albums to organize and showcase your favorite cards.
                    Collect and save your most prized cards to display them in your albums!
                """.trimIndent()
            )

            InfoSection(
                title = "Card Drop Rates",
                content = """
                    The odds of getting different types of cards are as follows:
                    - Common Cards: 50% chance
                    - Uncommon Cards: 35% chance
                    - Rare Cards: 12.5% chance
                    - Legendary Cards: 2.5% chance
                    
                    These odds apply each time you open a card pack. Good luck on your collection journey!
                """.trimIndent()
            )

            Spacer(modifier = Modifier.weight(1f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "©\nThomas Stopper, cc231012\nLeonie Kozak, cc231010\n\nSanrioTCG, 2025",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = Color(0xFF7687D3)
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }
}


@Composable
fun InfoSection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF7687D3)
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 16.sp,
                color = Color(0xFF555555)
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}