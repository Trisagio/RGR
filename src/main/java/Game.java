// класс с данными игры
public class Game {
    public boolean inGame = false;  // флаг начала игры
    public boolean dying = false; // флаг смерти игрока
    public final int BLOCK_SIZE = 24; // размеры поля
    public final int N_BLOCKS = 15;
    public final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE;
    public final int MAX_GHOSTS = 12; // максимальное количество призраков
    public int PACMAN_SPEED = 3; // скорость пакмена
    public boolean stoppac = false; // переменная отвечающая за остановку пакмена
    public int egpos = 0;   // расположение пасхального яйца
    public int N_GHOSTS = 1; // количество призраков в действительности
    public int lives, score; // жизни и очки
    public final int MAX_LIVES = 3, MAX_SCORE = 999; // максимальные жизни и очки
    public int[] dx, dy; // случайные перемещения для призраков
    public int[] ghost_x, ghost_y, ghost_dx, ghost_dy, ghostSpeed; // перемешение призраков
    public int pacman_x, pacman_y, pacmand_x, pacmand_y; // направление и координаты пакмена
    public int req_dx, req_dy; // направление анимации
    public int[] levelData = new int[N_BLOCKS * N_BLOCKS]; // массив с цифрами обозначающими данные карты 2-камен, 16-еда, 4-пасхальное яйцо
    public final int validSpeeds[] = {1, 2, 3, 4}; // скорости для призраков
    public final int maxSpeed = 6; // элемент в массиве скорости для призраков максимальный
    public int currentSpeed = 3; // скорость призрака в игре из массива под элементом 3
    public int[] screenData; // данные карты которые изменяются в реальном времени игроком
    public final int panelSize = 400; // размер игрового окна
    public boolean isfaster = false; // отвечает за ускорение пакмена
    public int scorePrice = 25; // цена восстановления жизней
}