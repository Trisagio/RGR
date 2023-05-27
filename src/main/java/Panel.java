import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Random;
import javax.swing.*;

import com.google.gson.Gson;

public class Panel extends JPanel implements ActionListener {
    // размеры
    private Dimension d;
    // шрифт
    private final Font smallFont = new Font("Arial", Font.BOLD, 14);
    // картинки призрака и очков жизней
    private Image heart, ghost;
    // картинки пакмена
    private Image up, down, left, right;
    // Таймер для задержки
    private Timer timer;
    // класс игры, в которой хранятся все данные игры
    private Game game;
    // конструктор класса Panel
    public Panel() {
        // инициализируем класс игры
        game = new Game();
        // загружаем изображения
        loadImages();
        // инициализируем переменные
        initVariables();
        // запускаем чтение с ввода пользователя
        addKeyListener(new InputAdapter());
        // фокусировка на компонент Jpanel
        setFocusable(true);
        // инициализируем игру
        initGame();
    }
    // загрузка изображений игры
    private void loadImages() {
        // при загрузке получаем абсолютный путь к проекту
        down = new ImageIcon(System.getProperty("user.dir") + "\\src\\images\\down.gif").getImage();
        up = new ImageIcon(System.getProperty("user.dir") + "\\src\\images\\up.gif").getImage();
        left = new ImageIcon(System.getProperty("user.dir") + "\\src\\images\\left.gif").getImage();
        right = new ImageIcon(System.getProperty("user.dir") + "\\src\\images\\right.gif").getImage();
        ghost = new ImageIcon(System.getProperty("user.dir") + "\\src\\images\\ghost.gif").getImage();
        heart = new ImageIcon(System.getProperty("user.dir") + "\\src\\images\\heart.png").getImage();
    }
    // инциализация переменных
    private void initVariables() {
        // инициализируем массив карты
        game.screenData = new int[game.N_BLOCKS * game.N_BLOCKS];
        // инициализируем размеры окна
        d = new Dimension(game.panelSize, game.panelSize);
        // инициализируем массивы призарков
        game.ghost_x = new int[game.MAX_GHOSTS];
        game.ghost_dx = new int[game.MAX_GHOSTS];
        game.ghost_y = new int[game.MAX_GHOSTS];
        game.ghost_dy = new int[game.MAX_GHOSTS];
        game.ghostSpeed = new int[game.MAX_GHOSTS];
        // инициализируем переменны для изменения положения пакмена по x и y
        game.dx = new int[4];
        game.dy = new int[4];
        // таймер с отрисовкой с задержкой 40
        timer = new Timer(40, this);
        // запускаем таймер
        timer.start();
    }
    // функция игры
    private void playGame(Graphics2D g2d) throws IOException {
        // если игрок умер, то вызываем функцию death
        if (game.dying) {
            death();
            // если не умер, то..
        } else {
            // вызываем функцию изменения положения пакмена
            movePacman();
            // отрисовка пакмена
            drawPacman(g2d);
            // движение призакров
            moveGhosts(g2d);
        }
    }
    // начальная заставка
    private void showIntroScreen(Graphics2D g2d) {
        String start = "Нажмите *пробел* для начала игры";
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect((d.width - 300) / 2, (d.height - 75) / 2, 270, 50);
        g2d.setColor(Color.GREEN);
        g2d.drawString(start, (d.width - 290) / 2, (d.height - 25) / 2);
    }
    // отрисовка интерфейса снизу с данными текущей игры
    private void drawInterface(Graphics2D g) {
        g.setFont(smallFont);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 360, game.SCREEN_SIZE + 16, 50);
        g.setColor(Color.GREEN);
        // записываем в буфер данные с очками
        String s = "Очки: " + game.score;
        // выводим очки
        g.drawString(s, game.SCREEN_SIZE / 2 + 96, game.SCREEN_SIZE + 16);
        // рисуем в цикле картинки с жизнями
        for (int i = 0; i < game.lives; i++) {
            g.drawImage(heart, i * 28 + 8, game.SCREEN_SIZE + 1, this);
        }
        // если собрано пасхальное яйцо, выводим сообщение снизу на экране
        if (game.egpos == -1) {
            g.setColor(new Color(180, 180, 30));
            String egg = "Секрет найден";
            g.drawString(egg, 89, game.SCREEN_SIZE + 16);
        }
    }
    // функция смерти
    private void death() throws IOException {
        // отнимаем жизнь
        game.lives--;
        // если жизней уже нет
        if (game.lives == 0) {
            // игра завершена
            game.inGame = false;
        }
        // продолжаем игру
        continueLevel();
    }
    // функция перемещения призраков
    private void moveGhosts(Graphics2D g2d) {

        int pos;
        int count;
        // перебираем призраков
        for (int i = 0; i < game.N_GHOSTS; i++) {
            if (game.ghost_x[i] % game.BLOCK_SIZE == 0 && game.ghost_y[i] % game.BLOCK_SIZE == 0) {
                pos = game.ghost_x[i] / game.BLOCK_SIZE + game.N_BLOCKS * (int) (game.ghost_y[i] / game.BLOCK_SIZE);
                count = 0;
                // различные случайные двжиения  относительно позиции на карте
                if ((game.screenData[pos] & 1) == 0 && game.ghost_dx[i] != 1) {
                    game.dx[count] = -1;
                    game.dy[count] = 0;
                    count++;
                }
                if ((game.screenData[pos] & 2) == 0 && game.ghost_dy[i] != 1) {
                    game.dx[count] = 0;
                    game.dy[count] = -1;
                    count++;
                }
                if ((game.screenData[pos] & 4) == 0 && game.ghost_dx[i] != -1) {
                    game.dx[count] = 1;
                    game.dy[count] = 0;
                    count++;
                }
                if ((game.screenData[pos] & 8) == 0 && game.ghost_dy[i] != -1) {
                    game.dx[count] = 0;
                    game.dy[count] = 1;
                    count++;
                }
                // получаем случайное число
                count = (int) (Math.random() * count);
                if (count > 3) {
                    count = 3; // если больше трех то 3, т.к. в массиве dx всего 4 элемента
                }
                // перебираем чтобы призрак не ушел за переделы экрана
                if ((game.dx[count] == 0 && game.dy[count] == -1 && (pos > -1 && pos < 15) ||
                        game.dx[count] == -1 && game.dy[count] == 0 && (pos == 0 || pos % 15 == 0) ||
                        game.dx[count] == 1 && game.dy[count] == 0 && (pos == 14 || pos == 29 || pos == 44 || pos == 59 || pos == 74
                                || pos == 89 || pos == 104 || pos == 119 || pos == 134 || pos == 149 || pos == 164 || pos == 179 || pos == 194
                                || pos == 209 || pos == 224) ||
                        game.dx[count] == 0 && game.dy[count] == 1 && pos > 209 && pos < 225)) {
                    game.dx[count] = 0; // если вероятность уйти есть, то скорость будет умножаться на 0, соответственно он не покинет зону игры
                    game.dy[count] = 0;
                }
                // передаем направления в i-й призрак
                game.ghost_dx[i] = game.dx[count];
                game.ghost_dy[i] = game.dy[count];

            }
            // скорость умножаем на направление - получаем перещемение призрака
            game.ghost_x[i] = game.ghost_x[i] + (game.ghost_dx[i] * game.ghostSpeed[i]);
            game.ghost_y[i] = game.ghost_y[i] + (game.ghost_dy[i] * game.ghostSpeed[i]);
            // рисуем призрака
            g2d.drawImage(ghost, game.ghost_x[i] + 1, game.ghost_y[i] + 1, this);
            // если призрак на позиции пакмена то он умирает
            if (game.pacman_x > (game.ghost_x[i] - 12) && game.pacman_x < (game.ghost_x[i] + 12) && game.pacman_y > (game.ghost_y[i] - 12)
                    && game.pacman_y < (game.ghost_y[i] + 12) && game.inGame) {

                game.dying = true;
            }
        }
    }

    private void movePacman() {
        int pos;
        int ch;
        if (game.pacman_x % game.BLOCK_SIZE == 0 && game.pacman_y % game.BLOCK_SIZE == 0) {
            // получение позиции
            pos = game.pacman_x / game.BLOCK_SIZE + game.N_BLOCKS * (int) (game.pacman_y / game.BLOCK_SIZE);
            // получение числа с массива данными карты
            ch = game.screenData[pos];
            // если настигнуто пасхальное яйцо
            if (pos == game.egpos) {
                // значит убираем его
                game.egpos = -1;
                // размещаем там камень
                game.screenData[pos] = 2;
                game.levelData[pos] = 2;
                // и увеличиваем количество очков на 75
                game.score += 75;
            }

            // если попал на камень
            if ((ch & 2) != 0) {
                game.dying = true;
            }
            // если попал на еду
            if ((ch & 16) != 0) {
                game.screenData[pos] = (ch & 15);
                game.score++;
            }
            // перемещение (сначала проверка на изменение анимации)
            if (game.req_dx != 0 || game.req_dy != 0) {
                game.pacmand_x = game.req_dx;
                game.pacmand_y = game.req_dy;
            }
            // Коллизия, проверка на возможный выход за пределы экрана
            if ((game.pacmand_x == 0 && game.pacmand_y == -1 && (pos > -1 && pos < 15) ||
                    game.pacmand_x == -1 && game.pacmand_y == 0 && (pos == 0 || pos % 15 == 0) ||
                    game.pacmand_x == 1 && game.pacmand_y == 0 && (pos == 14 || pos == 29 || pos == 44 || pos == 59 || pos == 74
                            || pos == 89 || pos == 104 || pos == 119 || pos == 134 || pos == 149 || pos == 164 || pos == 179 || pos == 194
                            || pos == 209 || pos == 224) ||
                    game.pacmand_x == 0 && game.pacmand_y == 1 && pos > 209 && pos < 225)||
                    game.stoppac) {
                // если это возможно, то указываем 0 перемещение
                game.pacmand_x = 0;
                game.pacmand_y = 0;
            }
        }
        // если скорость активирована, меняем ее
        game.PACMAN_SPEED = game.isfaster?6:3;
        // получаем новый координаты исходя из скорости умноженной на перемещение
        // (pacmand_x хранит значения -1, 0 или 1. Позволяет если -1 - повернуть влево, 0 - остановиться, 1 - повернуть вправо)
        // аналогично со второй переменной только по Y
        game.pacman_x = game.pacman_x + game.PACMAN_SPEED * game.pacmand_x;
        game.pacman_y = game.pacman_y + game.PACMAN_SPEED * game.pacmand_y;
    }
    // отрисовка пакмена
    private void drawPacman(Graphics2D g2d) {
        // меняем анимацию движения пакмена
        if (game.req_dx == -1) {    // если пакмен движется влево
            g2d.drawImage(left, game.pacman_x + 1, game.pacman_y + 1, this);
        } else if (game.req_dx == 1) { // если пакмен движется вправо
            g2d.drawImage(right, game.pacman_x + 1, game.pacman_y + 1, this);
        } else if (game.req_dy == -1) { // если пакмен движется вверх
            g2d.drawImage(up, game.pacman_x + 1, game.pacman_y + 1, this);
        } else { // если пакмен движется вниз
            g2d.drawImage(down, game.pacman_x + 1, game.pacman_y + 1, this);
        }
    }
    // отрисовка карты
    private void drawMaze(Graphics2D g2d) {
        short i = 0;
        int x, y;
        // перебираем все элементы игрового окна
        for (y = 0; y < game.SCREEN_SIZE; y += game.BLOCK_SIZE) {
            for (x = 0; x < game.SCREEN_SIZE; x += game.BLOCK_SIZE) {
                g2d.setColor(Color.DARK_GRAY);
                g2d.setStroke(new BasicStroke(5));
                // рисуем камни
                if ((game.levelData[i] == 2)) {
                    // закрашенный квадрат
                    g2d.fillRect(x, y, game.BLOCK_SIZE, game.BLOCK_SIZE);
                }
                // рисуем пасхалку
                if ((game.screenData[i] == 4)) {
                    g2d.setColor(Color.CYAN);
                    // закрашенный овал
                    g2d.fillOval(x + 10, y + 10, 6, 6);
                }
                // рисуем объекты за которые получаем очки
                if (game.screenData[i] == 16) {
                    g2d.setColor(Color.GREEN);
                    // закрашенный овал
                    g2d.fillOval(x + 10, y + 10, 6, 6);
                }

                i++;
            }
        }
    }
    // функция инициализации игры
    private void initGame() {
        // начальное количество жизней
        game.lives = game.MAX_LIVES;
        // начальное количество очков
        game.score = 0;
        // инициализация уровня
        initLevel();
        // количество призраков
        game.N_GHOSTS = 1;
        // скорость игры
        game.currentSpeed = 3;
    }
    // инициализация уровня
    private void initLevel() {
        // объект для генерации случайных чисел
        Random rnd = new Random();
        // количество пасхальных яиц - 1
        int eg = 1;
        // расстановка элементов на карте
        for (int i = 0, j = 0; i < 15; i++) {
            for (int k = 0; k < 15; k++, j++) {
                // если выпало 1, то это камень, иначе трава
                if (rnd.nextInt() % 4 == 1)
                    game.levelData[j] = 2;
                else
                    game.levelData[j] = 16;
                // если первый элемент, то устанавливаем как траву
                if (j == 0) game.levelData[j] = 16;
                // устанавливаем пасхальное яйцо
                if (eg == 1 && rnd.nextInt() % 4 == 1
                        && j > 2 && game.levelData[j - 1] != 2 && game.levelData[j + 1] != 2) {
                    game.levelData[j] = 4;
                    game.egpos = j;
                    eg--;
                }
            }
        }
        // записываем данные со сгенерированной карты в игровую, обновляемую
        for (int i = 0; i < game.N_BLOCKS * game.N_BLOCKS; i++) {
            game.screenData[i] = game.levelData[i];
        }
        // продолжение уровня
        continueLevel();
    }
    // продолжение уровня
    private void continueLevel() {
        int dx = 1;
        int random;
        for (int i = 0; i < game.N_GHOSTS; i++) {
            game.ghost_y[i] = 4 * game.BLOCK_SIZE; // стартовые позиции призраков
            game.ghost_x[i] = 4 * game.BLOCK_SIZE;
            game.ghost_dy[i] = 0;
            game.ghost_dx[i] = dx;
            dx = -dx;
            // случайные скорости призраков
            random = (int) (Math.random() * (game.currentSpeed + 1));
            if (random > game.currentSpeed) {
                random = game.currentSpeed;
            }
            game.ghostSpeed[i] = game.validSpeeds[random];
        }

        game.pacman_x = 0; // стартовая позиция пакмена
        game.pacman_y = 0;
        game.pacmand_x = 0; // направления пакмена
        game.pacmand_y = 0;
        game.req_dx = 0; // направления анимации
        game.req_dy = 0;
        game.dying = false; // пакмен живой
    }
    // функция отрисовки в панели
    public void paintComponent(Graphics g) {
        // передаем компонент
        super.paintComponent(g);
        // создем новый компонент 2д графики
        Graphics2D g2d = (Graphics2D) g;
        // устанавливаем цвет
        g2d.setColor(Color.WHITE);
        // рисуем квадрат
        g2d.fillRect(0, 0, d.width, d.height);
        // рисуем карту
        drawMaze(g2d);
        // рисуем интерфейс
        drawInterface(g2d);
        // рисуем остальные игровые элементы
        if (game.inGame) {
            try {
                playGame(g2d);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showIntroScreen(g2d); // если игра не начата, рисуем приветственный элемент
        }
        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }
    // обработка нажатий для игры и движений пакмена
    class InputAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            // получаем код
            int key = e.getKeyCode();
            if (game.inGame) {
                // движение влево
                if (key == KeyEvent.VK_LEFT) {  // движение влево
                    game.req_dx = -1;
                    game.req_dy = 0;
                } else if (key == KeyEvent.VK_RIGHT) { // движение вправо
                    game.req_dx = 1;
                    game.req_dy = 0;
                } else if (key == KeyEvent.VK_UP) { // движение вверх
                    game.req_dx = 0;
                    game.req_dy = -1;
                } else if (key == KeyEvent.VK_DOWN) { // движение вниз
                    game.req_dx = 0;
                    game.req_dy = 1;
                } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) { // отмена игры
                    game.inGame = false;
                } else if (key == KeyEvent.VK_W) { // ускорение или замедление
                    game.isfaster = !game.isfaster;
                } else if (key == KeyEvent.VK_S) { // остановка или движение
                    game.stoppac=!game.stoppac;
                } else if (key == KeyEvent.VK_A) {
                    // если число жизней не максимально, доводим до максимального по текущей цене
                    while (game.lives != game.MAX_LIVES && game.score > game.scorePrice) {
                        game.score -= game.scorePrice;
                        game.lives++;
                    }
                } else if (key == KeyEvent.VK_D) {  // перейти на следующий уровень
                    if (game.score > game.scorePrice * 3) {
                        game.score -= 50;
                        // добавляем очков, если собрана пасхалка
                        if (game.egpos == -1)  game.score += 50;
                        // добавляем призраков
                        if (game.N_GHOSTS < game.MAX_GHOSTS) game.N_GHOSTS++;
                        // добавляем скорости игре
                        if (game.currentSpeed < game.maxSpeed) game.currentSpeed++;
                        // обновляем уровень
                        initLevel();
                    }
                } else if (key == KeyEvent.VK_C) { // чит-код)
                    game.score += 4 * game.scorePrice;
                }
            } else {
                if (key == KeyEvent.VK_SPACE) { // запуск игры
                    game.inGame = true; // игра запущена
                    initGame(); // инициализируем игру
                }
            }
        }
    }
    // стандартная функция перерисовки
    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
    // функция сохранения игры
    public void SaveFile(String path) throws IOException {
        // Создаем объект для работы с json форматом
        Gson gson = new Gson();
        // создаем объект файл для создания файла указывая путь в качестве аргумента
        File file = new File(path);
        // создаем файл
        file.createNewFile();
        // преобразуем в json формат посредством объекта gson
        String json = gson.toJson(game);
        // Создаем объект для буферизированной записи в файл указывая объект FileWriter с путем
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            // производим запись в файл
            bw.write(json);
        }
    }
    // функция загрузки сохранения
    public void LoadSave(String path) throws FileNotFoundException {
        // Создаем объект gson для дессериализации
        Gson gson = new Gson();
        // получаем объект Game благодаря объекту Gson, указывая в качестве аргументов путь и тип класса
        game = gson.fromJson(new FileReader(path), Game.class);
    }
}