/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pokergameclass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

/**
 *
 * @author Rajath Jain
 */

enum TypeOfPokerHands{
    HIGH_CARD,
    ONE_PAIR,
    TWO_PAIR,
    THREE_OF_A_KIND,
    STRAIGHT,
    FLUSH,
    FULL_HOUSE,
    FOUR_OF_A_KIND,
    STRAIGHT_FLUSH
}

class Card {
    public static final int maxRankValue = 13;
    private int rank, suit;
    private static final String[] suits = {"Hearts", "Clubs", "Diamonds", "Spades"};
    private static final String[] ranks = {"Ace", "2", "3", "4", "5", "6", "7", "8", 
        "9", "10", "Jack", "Queen", "King"};
    
    public static String getRankAsString(int rank) {
        return ranks[rank % maxRankValue];
    }
    
    Card (int rank, int suit) {
        this.rank = rank;
        this.suit = suit;
    }
    
    @Override
    public String toString() {
        return ranks[rank % maxRankValue] + " of " + suits[suit];
    }
    
    public int getRank() {
        return this.rank;
    }
    
    public int getSuit() {
        return this.suit;
    }
    
    
}

class Deck {
    private ArrayList<Card> cards;
    
    Deck() {
        cards = new ArrayList<>();
        int index1, index2;
        Random random = new Random();
        
        //Add all cards to Deck
        for(int suit = 0; suit < 4; suit++)
            for(int rank = 0; rank < 13; rank++)
                cards.add(new Card(rank, suit));
        
        //Shuffle cards in Deck
//        int size = cards.size() - 1;
//        Card temp;
//        for(int i = 0; i < 100; i++) {
//            index1 = random.nextInt(size);
//            index2 = random.nextInt(size);
//            //System.out.println(index1+" and " +index2);
//            temp = (Card) cards.get(index2);
//            cards.set(index2, cards.get(index1));
//            cards.set(index1,temp);
//        }
        //Collections.shuffle(cards);
        Collections.shuffle(cards, random);
    }
    
    public Card drawFromDeck() {
        return cards.remove(0);
    }
    
    public int getTotalCards() {
        return cards.size();
    }
}

class Hand {
    private Card[] cardsInHand;
    int[] valueOfHand;
    TypeOfPokerHands handType;
    int[] rankCount;
    
    int sameCards, sameCards2;
    int groupRank, groupRank2;
    int topStraightValue;
    boolean isStraight, isFlush;
    
    Hand(Deck deck) {
        cardsInHand = new Card[5];
        valueOfHand = new int[5];
        
        sameCards = 1;
        sameCards2 = 1;
        groupRank = -1;
        groupRank2 = -1;
        isFlush = true;
        isStraight = false;
        topStraightValue = 0;
        
        for(int i = 0; i < 5; i++) {
            cardsInHand[i] = deck.drawFromDeck();
            valueOfHand[i] = -1;
        }
    }
    
    Hand(Card[] cards) {
        cardsInHand = new Card[5];
        valueOfHand = new int[5];
        
        sameCards = 1;
        sameCards2 = 1;
        groupRank = -1;
        groupRank2 = -1;
        isFlush = true;
        isStraight = false;
        topStraightValue = 0;
        
        System.arraycopy(cards, 0, cardsInHand, 0, 5);
    }
    
    public void getDataOfHand() {
        rankCount = new int[13];
        
        for(int i = 0; i < 13; i++)
            rankCount[i] = 0;
        
        for(Card card : cardsInHand)
            rankCount[card.getRank()]++;
        /*
        System.out.println("ranks");
        for(int i:rankCount)
            System.out.print(i + " ");
        System.out.println();
        */
        for(int rank = 0; rank < 13; rank++) {
            if(rankCount[rank] >= sameCards && rank > groupRank) {
                if(sameCards != 1) {
                    sameCards2 = sameCards;
                    groupRank2 = groupRank;
                }
                groupRank = rank;
                sameCards = rankCount[rank];
            }
            else if(rankCount[rank] > sameCards2) {
                sameCards2 = rankCount[rank];
                groupRank2 = rank;
            }
        }
        
        for(int i = 0; i < 4; i++) {
            if(cardsInHand[i].getSuit() != cardsInHand[i+1].getSuit()) {
                isFlush = false;
                break;
            }
        }
        
        for(int rank = 0; rank < 9; rank++) {
            if(rankCount[rank] == 1 && rankCount[rank + 1] == 1 && 
                    rankCount[rank + 2] == 1 && rankCount[rank + 3] == 1 && 
                    rankCount[rank + 4] == 1) {
                isStraight = true;
                topStraightValue = rank + 4;
            }
            if(rankCount[9] == 1 && rankCount[10] == 1 && 
                    rankCount[11] == 1 && rankCount[12] == 1 && 
                    rankCount[0] == 1) {
                isStraight = true;
                topStraightValue = 13;
            }
        }
        
        evaluateHand();
    }
    
    private void evaluateHand() {
        int[] orderedRanks = new int[5];
        int index = 0;
        if(rankCount[0] == 1) {
            orderedRanks[index] = Card.maxRankValue;
            index++;
        }
        for(int rank = 12; rank >= 1; rank--) {
            if(rankCount[rank] == 1) {
                orderedRanks[index] = rank;
                index++;
            }
        }
        /*
        System.out.println("ordered ranks");
        for(int i:orderedRanks)
            System.out.print(i + " ");
        System.out.println();
        */
        switch (sameCards) {
            case 1:
                handType = TypeOfPokerHands.HIGH_CARD;
                System.arraycopy(orderedRanks, 0, valueOfHand, 0, 5);
                break;
            case 2:
                if(sameCards2 == 1) {
                    handType = TypeOfPokerHands.ONE_PAIR;
                    valueOfHand[0] = groupRank;
                    valueOfHand[1] = orderedRanks[0];
                    valueOfHand[2] = orderedRanks[1];
                    valueOfHand[3] = orderedRanks[2];
                }
                else if(sameCards2 == 2) {
                    handType = TypeOfPokerHands.TWO_PAIR;
                    valueOfHand[0] = groupRank;
                    valueOfHand[1] = groupRank2;
                    valueOfHand[2] = orderedRanks[0];
                }   break;
            case 3:
                if(sameCards2 == 1) {
                    handType = TypeOfPokerHands.THREE_OF_A_KIND;
                    valueOfHand[0] = groupRank;
                    valueOfHand[1] = orderedRanks[0];
                    valueOfHand[2] = orderedRanks[1];
                }
                else if(sameCards2 == 2) {
                    handType = TypeOfPokerHands.FULL_HOUSE;
                    valueOfHand[0] = groupRank;
                    valueOfHand[1] = groupRank2;
                }   break;
            case 4:
                handType = TypeOfPokerHands.FOUR_OF_A_KIND;
                valueOfHand[0] = groupRank;
                valueOfHand[1] = orderedRanks[0];
                break;
            default:
                break;
        }
        
        if(isStraight) {
            handType = TypeOfPokerHands.STRAIGHT;
            valueOfHand[0] = topStraightValue;
        }
        
        if(isFlush) {
            if(isStraight) {
                handType = TypeOfPokerHands.STRAIGHT_FLUSH;
                valueOfHand[0] = topStraightValue;
            }
            else {
                handType = TypeOfPokerHands.FLUSH;
                System.arraycopy(orderedRanks, 0, valueOfHand, 0, 5);
            }
        }
    }
    
    public Card getCard(int index) {
        return cardsInHand[index];
    }
    
    public void displayAllCardsInHand() {
        for(int i = 0;i < 5; i++)
            System.out.println(cardsInHand[i]);
        System.out.println();
    }
    
    public void displayHand() {
        System.out.println(handType);
        System.out.println(Card.getRankAsString(valueOfHand[0]));
        System.out.println(Card.getRankAsString(valueOfHand[1]));
    }
}

class Player {
    private Card[] playerCards;
    
    Player() {
        playerCards = new Card[2];
        for(int i = 0; i < 2; i++)
            playerCards[i] = null;
    }
    
    @Override
    public String toString() {
        return playerCards[0] + " and " + playerCards[1];
    }
    
    public void setCard(Deck deck, int index) {
        playerCards[index] = deck.drawFromDeck();
    }
    
    public Card getCard(int index) {
        return playerCards[index];
    }
}

class HandComparator {
    private Hand hand1, hand2;
    
    HandComparator(Hand hand1, Hand hand2) {
        this.hand1 = hand1;
        this.hand2 = hand2;
    }
    
    HandComparator() {
        this.hand1 = null;
        this.hand2 = null;
    }
    
    public int compareHands(Hand hand1, Hand hand2) {
        if(hand1.handType.compareTo(hand2.handType) > 0) {
            //System.out.println("Hand1 wins");
            return 1;
        }
        else if(hand1.handType.compareTo(hand2.handType) < 0) {
            //System.out.println("Hand2 wins");
            return -1;
        }
        else {
            for(int i = 0; i < 5; i++) {
                if(hand1.valueOfHand[i] > hand2.valueOfHand[i]) {
                    //System.out.println("Hand1 wins");
                    return 1;
                }
                else if(hand1.valueOfHand[i] < hand2.valueOfHand[i]) {
                    //System.out.println("Hand2 wins");
                    return -1;
                }
            }
        }
        System.out.println("Draw. Split pot");
        return 0;
    }
    
    public Hand comparePlayerHand(Hand hand, Player player) {
        ArrayList<Card> allCards = new ArrayList<>();
        for(int i = 0; i < 5; i++)
            allCards.add(hand.getCard(i));
        for(int i = 0; i < 2; i++)
            allCards.add(player.getCard(i));
        
        hand.getDataOfHand();
        Hand bestHand = hand;
        for(int i = 0; i < 6; i++) {
            for(int j = i + 1; j < 7; j++) {
                //System.out.println(i + " " + j);
                Card[] cards = getFiveCards(allCards, i, j);
                //System.out.println(allCards.size() + " length " + cards.length);
                Hand currentHand = new Hand(cards);
                currentHand.getDataOfHand();
                if(compareHands(bestHand, currentHand) < 0)
                    bestHand = currentHand;
            }
        }
        return bestHand;
    }
    
    public void compareAllHands(Hand hand, Player[] players) {
        Hand bestPlayerHand;
        int bestPlayer = 0;
        bestPlayerHand = comparePlayerHand(hand, players[0]);
        System.out.println("after computation");
        hand.displayAllCardsInHand();
        System.out.println("player 1");
        bestPlayerHand.displayAllCardsInHand();
        bestPlayerHand.displayHand();
        System.out.println();
        
        for(int i = 1; i < players.length; i++) {
            Hand tmpHand = comparePlayerHand(hand, players[i]);
            System.out.println("player " + (i + 1));
            tmpHand.displayAllCardsInHand();
            tmpHand.displayHand();
            System.out.println();
            if(compareHands(bestPlayerHand, tmpHand) < 0) {
                bestPlayerHand = tmpHand;
                bestPlayer = i;
            }
        }
        
        System.out.println("Player" + (bestPlayer + 1) + "wins");
        bestPlayerHand.displayHand();
    }
    
    private Card[] getFiveCards(ArrayList<Card> allCards, int index1, int index2) {
        ArrayList<Card> tmpCards = new ArrayList<>();
        tmpCards.addAll(allCards);
        //System.out.println("tmp " + tmpCards.size());
        tmpCards.remove(index1);
        tmpCards.remove(index2-1);
        return tmpCards.toArray(new Card[tmpCards.size()]);
    }
}

public class PokerGameClass {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Card[] cards = new Card[5];
        int hearts = 0, clubs = 1, diamonds = 2, spades = 3;
        cards[0] = new Card(11,hearts);
        cards[1] = new Card(11,diamonds);
        cards[2] = new Card(11,clubs);
        cards[3] = new Card(0,spades);
        cards[4] = new Card(0,clubs);
        Hand handWithCards = new Hand(cards);
//        handWithCards.getDataOfHand();
//        handWithCards.displayAllCardsInHand();
//        handWithCards.displayHand();
        
        Deck deck = new Deck();
        
        //Hand handWithDeck2 = new Hand(deck);
        //handWithDeck.getDataOfHand();
        //handWithDeck2.getDataOfHand();
        //handWithDeck.displayHand();
        //handWithDeck2.displayHand();
        HandComparator handComparator = new HandComparator();
        //int ret = handComparator.compareHands(handWithDeck, handWithDeck2);
        Player[] players = new Player[2];
        players[0] = new Player();
        players[1] = new Player();
        players[0].setCard(deck, 0);
        players[1].setCard(deck, 0);
        players[0].setCard(deck, 1);
        players[1].setCard(deck, 1);
        Hand handWithDeck = new Hand(deck);
        System.out.println("Hand on table");
        handWithDeck.displayAllCardsInHand();
        System.out.println("Player Cards");
        System.out.println(players[0]);
        System.out.println(players[1]);
        handComparator.compareAllHands(handWithDeck, players);
//        Card card = new Deck().drawFromDeck();
//        System.out.println(card);
    }
    
}
