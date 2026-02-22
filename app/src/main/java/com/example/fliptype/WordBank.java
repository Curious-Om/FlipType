package com.example.fliptype;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class WordBank {

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    public enum Category {
        GENERAL("General"),
        TECH("Tech"),
        FOOD("Food"),
        ANIMALS("Animals"),
        SPORTS("Sports"),
        COUNTRIES("Countries");

        public final String displayName;
        Category(String displayName) { this.displayName = displayName; }
    }

    // ── GENERAL ─────────────────────────────────────────────────
    private static final String[] GEN_EASY = {
            "OM", "CAT", "DOG", "SUN", "RUN", "FUN", "BIG", "RED", "TOP", "CUP", "HAT",
            "BAT", "MAP", "PEN", "BOX", "BUS", "JAM", "NET", "ZIP", "FOG", "HUG",
            "ICE", "JOY", "KEY", "LOG", "MOP", "NUT", "OWL", "PIG", "RAT", "SKY",
            "TEN", "VAN", "WAX", "ARM", "BED", "COW", "DIG", "EGG", "FAN", "GUM",
            "HIT", "INK", "JAR", "KIT", "LIP", "MUD", "NAP", "OAK", "RUG", "TAP",
            "BIRD", "CAKE", "DOOR", "FAST", "GAME", "HAND", "JUMP", "KING", "LAMP",
            "MOON", "NEST", "POOL", "RAIN", "STAR", "TREE", "WAVE", "ZERO", "BELL",
            "CARD", "DRUM", "FISH", "GOLD", "HILL", "IRON", "KITE", "LEAF", "MILK"
    };
    private static final String[] GEN_MEDIUM = {
            "RAHUL", "APPLE", "GREEN", "STORM", "BRAVE", "CHESS", "DANCE", "EAGLE", "FLAME",
            "GRAPE", "HOUSE", "JUICE", "KNIFE", "LEMON", "MOUSE", "NIGHT", "OCEAN",
            "PIANO", "QUEEN", "RIVER", "SNAKE", "TRAIN", "ULTRA", "VOICE", "WATER",
            "YACHT", "ZEBRA", "BEACH", "CLOUD", "DREAM", "EARTH", "FRESH", "GIANT",
            "HEART", "IMAGE", "JOKER", "LIGHT", "MAGIC", "NORTH", "OLIVE", "PEARL",
            "QUIET", "ROBOT", "SPACE", "TIGER", "UNDER", "VIVID", "WORLD", "YOUNG",
            "BRIDGE", "CASTLE", "DESERT", "FROZEN", "GARDEN", "HEALTH", "INSECT",
            "JACKET", "KITTEN", "MARBLE", "ORANGE", "PLANET", "QUARTZ", "SILVER",
            "TEMPLE", "USEFUL", "VALLEY", "WONDER", "BREEZE", "CANDLE", "FINGER"
    };
    private static final String[] GEN_HARD = {
            "ANDROID", "BICYCLE", "CAPTAIN", "DOLPHIN", "ECLIPSE", "FACTORY", "GIRAFFE",
            "HARMONY", "JAWBONE", "JOURNEY", "KITCHEN", "LIBRARY", "MAXIMUM", "NATURAL",
            "OCTOPUS", "PENGUIN", "QUARTER", "RAINBOW", "SOLDIER", "THUNDER", "UNIFORM",
            "VILLAGE", "WHISPER", "CRYSTAL", "BLANKET", "CHAMBER", "DIAMOND", "ELEMENT",
            "FICTION", "GENUINE", "HIGHWAY", "IMAGINE", "JAVELIN", "KINGDOM", "MONSTER",
            "NUCLEUS", "OPINION", "PLASTIC", "QUANTUM", "REGULAR", "SUPREME", "TROUBLE",
            "VOLCANO", "WESTERN", "EXTREME", "NETWORK", "CABINET", "CURTAIN", "FANTASY",
            "HORIZON", "LANTERN", "MAMMOTH", "NEUTRAL", "ORBITAL", "PERFECT", "SHELTER",
            "THERMAL", "UMBRELLA", "VARIABLE", "ABSOLUTE", "COMPLETE", "FEEDBACK"
    };

    // ── TECH ────────────────────────────────────────────────────
    private static final String[] TECH_EASY = {
            "APP", "BUG", "CPU", "RAM", "USB", "CSS", "GIT", "HEX", "LOG", "NET",
            "SQL", "TAB", "URL", "WEB", "BYTE", "CHIP", "CODE", "DATA", "DISK", "FILE",
            "HACK", "HTML", "JAVA", "LINK", "NODE", "PING", "PORT", "ROOT", "SCAN", "WIFI"
    };
    private static final String[] TECH_MEDIUM = {
            "CACHE", "CLASS", "CLOUD", "DEBUG", "EMAIL", "FLOAT", "LINUX", "MOUSE", "PIXEL",
            "PROXY", "QUERY", "REACT", "REGEX", "TOKEN", "VIRUS", "ADMIN", "ARRAY", "BLOCK",
            "CLONE", "DRIVER", "ENCODE", "FILTER", "KERNEL", "LAPTOP", "MEMORY", "OUTPUT",
            "PLUGIN", "PYTHON", "ROUTER", "SCRIPT", "SERVER", "SOCKET", "STREAM", "SWITCH"
    };
    private static final String[] TECH_HARD = {
            "ANDROID", "BACKEND", "BITCOIN", "BOOLEAN", "BROWSER", "CLUSTER", "COMPILE",
            "CONSOLE", "DATASET", "DEFAULT", "DIGITAL", "DISPLAY", "ENCRYPT", "FIREWALL",
            "GATEWAY", "HASHING", "INSTALL", "KEYWORD", "LIBRARY", "MACHINE", "MALWARE",
            "MONITOR", "NETWORK", "OFFLINE", "PROGRAM", "RUNTIME", "SILICON", "STORAGE",
            "TOOLBAR", "UPGRADE", "VIRTUAL", "WEBSITE", "ALGORITHM", "DEVELOPER"
    };

    // ── FOOD ────────────────────────────────────────────────────
    private static final String[] FOOD_EASY = {
            "EGG", "FIG", "HAM", "JAM", "NUT", "OAT", "PIE", "RYE", "SOY", "TEA",
            "YAM", "BEET", "CAKE", "CORN", "CRAB", "FISH", "LAMB", "LIME", "MEAT",
            "MILK", "MINT", "PEAR", "PLUM", "PORK", "RICE", "SALT", "SOUP", "TACO"
    };
    private static final String[] FOOD_MEDIUM = {
            "APPLE", "BACON", "BERRY", "BREAD", "CANDY", "CREAM", "CURRY", "FLOUR",
            "GRAPE", "GUAVA", "HONEY", "KEBAB", "LEMON", "MANGO", "MELON", "OLIVE",
            "ONION", "PASTA", "PEACH", "PIZZA", "SALAD", "SAUCE", "SPICE", "STEAK",
            "SUGAR", "SUSHI", "TOAST", "WHEAT"
    };
    private static final String[] FOOD_HARD = {
            "ALMOND", "AVOCADO", "BISCUIT", "BURRITO", "CASHEW", "CHICKEN", "COCONUT",
            "CRACKER", "CUSTARD", "LOBSTER", "MUFFIN", "NOODLES", "PANCAKE", "POPCORN",
            "PRETZEL", "PUMPKIN", "RAISIN", "SAUSAGE", "SEAFOOD", "SPINACH", "VANILLA", "WAFFLE"
    };

    // ── ANIMALS ─────────────────────────────────────────────────
    private static final String[] ANIMAL_EASY = {
            "ANT", "APE", "BAT", "BEE", "CAT", "COW", "DOG", "EEL", "ELK", "EMU",
            "FOX", "HEN", "HOG", "OWL", "PIG", "RAM", "RAT", "YAK", "BEAR", "BIRD",
            "BULL", "CLAM", "CRAB", "CROW", "DEER", "DOVE", "DUCK", "FROG", "GOAT",
            "HAWK", "LION", "LYNX", "MOLE", "MOTH", "MULE", "SEAL", "SWAN", "TOAD", "WOLF"
    };
    private static final String[] ANIMAL_MEDIUM = {
            "BISON", "CAMEL", "COBRA", "CRANE", "EAGLE", "GECKO", "GOOSE", "HORSE",
            "HYENA", "KOALA", "LEMUR", "LLAMA", "MOOSE", "MOUSE", "OTTER", "PANDA",
            "QUAIL", "RAVEN", "ROBIN", "SHARK", "SHEEP", "SLOTH", "SNAIL", "SNAKE",
            "SQUID", "STORK", "TIGER", "TROUT", "VIPER", "WHALE", "ZEBRA"
    };
    private static final String[] ANIMAL_HARD = {
            "BUFFALO", "CARIBOU", "CHEETAH", "CHICKEN", "CRICKET", "DOLPHIN", "GAZELLE",
            "GIRAFFE", "GORILLA", "HAMSTER", "HERRING", "IGUANA", "JAGUAR", "LEOPARD",
            "LOBSTER", "MAMMOTH", "NARWHAL", "OCTOPUS", "PANTHER", "PEACOCK", "PELICAN",
            "PENGUIN", "RACCOON", "ROOSTER", "SPARROW", "VULTURE", "WALLABY"
    };

    // ── SPORTS ──────────────────────────────────────────────────
    private static final String[] SPORT_EASY = {
            "BAT", "BOW", "CUP", "GYM", "HIT", "HOP", "JOG", "NET", "OAR", "PIN",
            "RUN", "SET", "SKI", "WIN", "BALL", "BOUT", "CLUB", "DART", "DIVE", "DUNK",
            "FOUL", "GOAL", "GRIP", "KICK", "LANE", "PUCK", "PUNT", "RACE", "RINK",
            "SHOT", "SPIN", "SURF", "TEAM", "TOSS", "YOGA"
    };
    private static final String[] SPORT_MEDIUM = {
            "BLOCK", "CATCH", "CHASE", "CLIMB", "COACH", "COURT", "CYCLE", "DERBY",
            "DRIVE", "FENCE", "FIELD", "GLOVE", "GUARD", "KAYAK", "MATCH", "PITCH",
            "POINT", "RALLY", "RELAY", "RIDER", "ROUND", "RUGBY", "SCORE", "SERVE",
            "SKATE", "SPORT", "SQUAD", "SQUAT", "STUNT", "SWING", "THROW", "TRACK", "VAULT"
    };
    private static final String[] SPORT_HARD = {
            "ARCHERY", "BATTING", "BOWLING", "CRICKET", "DRIBBLE", "FENCING", "FITNESS",
            "FORMULA", "JAVELIN", "HURDLES", "LACROSSE", "MARATHON", "NETBALL", "OFFENSE",
            "PENALTY", "PLAYOFF", "RACQUET", "REFEREE", "SAILING", "SKATING", "SURFING",
            "SWIMMER", "WRESTLE", "CYCLING", "DEFENSE"
    };

    // ── COUNTRIES ────────────────────────────────────────────────
    private static final String[] COUNTRY_EASY = {
            "CHAD", "CUBA", "FIJI", "IRAN", "IRAQ", "LAOS", "MALI", "OMAN", "PERU", "TOGO"
    };
    private static final String[] COUNTRY_MEDIUM = {
            "BENIN", "CHILE", "CHINA", "CONGO", "EGYPT", "GABON", "GHANA", "HAITI",
            "INDIA", "ITALY", "JAPAN", "KENYA", "KOREA", "LIBYA", "MALTA", "NAURU",
            "NEPAL", "NIGER", "QATAR", "SAMOA", "SPAIN", "SUDAN", "SYRIA", "TONGA", "YEMEN"
    };
    private static final String[] COUNTRY_HARD = {
            "ALGERIA", "ANDORRA", "AUSTRIA", "BAHAMAS", "BAHRAIN", "BELARUS", "BELGIUM",
            "BOLIVIA", "BURUNDI", "CAMBODIA", "COMOROS", "CROATIA", "DENMARK", "ECUADOR",
            "ERITREA", "ESTONIA", "FINLAND", "GEORGIA", "GERMANY", "GRENADA", "HUNGARY",
            "ICELAND", "IRELAND", "JAMAICA", "LEBANON", "LESOTHO", "LIBERIA", "MOLDOVA",
            "MOROCCO", "MYANMAR", "NAMIBIA", "NIGERIA", "NORWAY", "PAKISTAN", "PANAMA",
            "ROMANIA", "SENEGAL", "SOMALIA", "SWEDEN", "TUNISIA", "UKRAINE", "URUGUAY",
            "VANUATU", "VIETNAM", "ZAMBIA"
    };

    private final Difficulty difficulty;
    private final List<String> wordPool;
    private int currentIndex;

    public WordBank(Difficulty difficulty) {
        this(difficulty, Category.GENERAL, null);
    }

    public WordBank(Difficulty difficulty, Category category) {
        this(difficulty, category, null);
    }

    public WordBank(Difficulty difficulty, Category category, Long seed) {
        this.difficulty = difficulty;
        String[] words = getWords(category, difficulty);
        wordPool = new ArrayList<>(Arrays.asList(words));
        if (seed != null) {
            Collections.shuffle(wordPool, new Random(seed));
        } else {
            Collections.shuffle(wordPool);
        }
        currentIndex = 0;
    }

    private static String[] getWords(Category category, Difficulty difficulty) {
        switch (category) {
            case TECH:
                switch (difficulty) {
                    case EASY:   return TECH_EASY;
                    case MEDIUM: return TECH_MEDIUM;
                    case HARD:   return TECH_HARD;
                }
            case FOOD:
                switch (difficulty) {
                    case EASY:   return FOOD_EASY;
                    case MEDIUM: return FOOD_MEDIUM;
                    case HARD:   return FOOD_HARD;
                }
            case ANIMALS:
                switch (difficulty) {
                    case EASY:   return ANIMAL_EASY;
                    case MEDIUM: return ANIMAL_MEDIUM;
                    case HARD:   return ANIMAL_HARD;
                }
            case SPORTS:
                switch (difficulty) {
                    case EASY:   return SPORT_EASY;
                    case MEDIUM: return SPORT_MEDIUM;
                    case HARD:   return SPORT_HARD;
                }
            case COUNTRIES:
                switch (difficulty) {
                    case EASY:   return COUNTRY_EASY;
                    case MEDIUM: return COUNTRY_MEDIUM;
                    case HARD:   return COUNTRY_HARD;
                }
            case GENERAL:
            default:
                switch (difficulty) {
                    case EASY:   return GEN_EASY;
                    case MEDIUM: return GEN_MEDIUM;
                    case HARD:   return GEN_HARD;
                }
        }
        return GEN_MEDIUM;
    }

    public String getNextWord() {
        if (currentIndex >= wordPool.size()) {
            Collections.shuffle(wordPool);
            currentIndex = 0;
        }
        return wordPool.get(currentIndex++);
    }

    public static String reverse(String word) {
        return new StringBuilder(word).reverse().toString();
    }

    public int getTimePerWord() {
        switch (difficulty) {
            case EASY:   return 8000;
            case HARD:   return 5000;
            case MEDIUM:
            default:     return 6000;
        }
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }
}
