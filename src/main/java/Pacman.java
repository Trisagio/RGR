import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Pacman extends JFrame {
    // меню
    JMenuBar jm;
    JMenu file, help;
    // элементы меню в окне
    JMenuItem fileSave;
    JMenuItem loadSave;
    JMenuItem openHelp;
    JMenuItem openAdvice;
    // панель на которой разворачивается игра
    Panel game;

    public Pacman() {
        // создаем панель игры
        game = new Panel();
        // добавляем в окно
        add(game);
        // создаем меню
        CreateMenu();
    }

    public static void main(String[] argc) {
        // создаем класс объекта унаследованного у окна
        Pacman pc = new Pacman();
        // делаем видимым
        pc.setVisible(true);
        // задаем титульное название окна
        pc.setTitle("Mr. Pacman");
        // задаем размеры окна
        pc.setSize(380, 450);
        // стандартная операция на закрытие
        pc.setDefaultCloseOperation(EXIT_ON_CLOSE);
        // центрируем окно на экране
        pc.setLocationRelativeTo(null);
    }

    private void CreateMenu() {
        // создаем верхнее меню
        jm = new JMenuBar();
        // создаем опции для меню сверху
        file = new JMenu("Файл");
        help = new JMenu("Справка");
        // подэлементы опции
        fileSave = new JMenuItem("Сохранить");
        loadSave = new JMenuItem("Загрузить");
        openHelp = new JMenuItem("Управление");
        openAdvice = new JMenuItem("Советы");
        // добавляем подэлементы в опции
        file.add(fileSave);
        file.add(loadSave);
        help.add(openHelp);
        help.add(openAdvice);
        // добавляем опцию в меню
        jm.add(file);
        jm.add(help);
        // устанавливаем меню
        setJMenuBar(jm);
        // действия на нажатие пункта помощь
        openHelp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Помощь");
                // вызываем диалоговое окно
                JOptionPane.showMessageDialog(null,
                        new String[]{"Управление",
                                "W - ускорение движения (вкл./выкл.)",
                                "S - остановка движения (вкл./выкл.)",
                                "A - обновление количества жизней (25 оч./жизнь)",
                                "D - принудительный переход на следующий уровень",
                                "Up, Down, Left, Right - направления движения",
                                "ESC - конец раунда",
                                "\nАвтор",
                                "Ложников Алексей",
                                "Выполнено в рамках учебного проекта по проект. и тест. ПО"},
                        "Мануал",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        // действия при нажатии на пункт меню советы
        openAdvice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Cоветы");
                // вызываем диалоговое окно
                JOptionPane.showMessageDialog(null,
                        new String[]{"Советы",
                                "Чем больше очков - тем лучше!",
                                "Найди секрет и получи ценные призы.",
                                "Опасайся камней, жуй травку и не беспокойся о стенках.",
                                "Остерегайся призраков.",
                                "\nАвтор",
                                "Ложников Алексей",
                                "Выполнено в рамках учебного проекта по проект. и тест. ПО"},
                        "Мануал",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        // действия на пункт сохранения
        fileSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                System.out.println("Сохранение игры");
                // создаем диалоговое окно
                FileDialog fd = new FileDialog(new JFrame());
                // делаем видимым
                fd.setVisible(true);
                // получаем файлы
                File[] f = fd.getFiles();
                // если было прописано что либо
                if (f.length > 0) {
                    System.out.println(fd.getFiles()[0].getAbsolutePath());
                    try {
                        // передаем путь в класс в котором осуществляется запись сохранения
                        game.SaveFile(fd.getFiles()[0].getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        // действия на пункт загрузки сохранения
        loadSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                System.out.println("Открытие файла сохранения");
                try {
                    // открываем диалоговое окно - проводник
                    FileDialog fd = new FileDialog(new JFrame());
                    fd.setVisible(true);
                    // получаем файл
                    File[] f = fd.getFiles();
                    // если он был выбран
                    if (f.length > 0) {
                        // получаем путь к нему и передаем в класс который производит чтение с файла сохранения и запуск
                        System.out.println(fd.getFiles()[0].getAbsolutePath());
                        game.LoadSave(fd.getFiles()[0].getAbsolutePath());
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
