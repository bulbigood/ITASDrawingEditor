package ui;

import core.Clipboard;
import core.FileData;
import core.KeyListener;
import core.Log;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import static core.DrawingArea.*;
import static ui.MainWindow.*;

/**
 * Строка меню
 */
public class MenuBar extends JMenuBar {

    /**
     * Меню "Файл"
     */
    private JMenu menuFile = new JMenu();
    /**
     * Элемент меню "Файл" >> "Новый файл"
     */
    private JMenuItem menuItemNew = new JMenuItem();
    /**
     * Элемент меню "Файл" >> "Открыть файл"
     */
    private JMenuItem menuItemOpen = new JMenuItem();
    /**
     * Элемент меню "Файл" >> "Сохранить файл"
     */
    private JMenuItem menuItemSaveAs = new JMenuItem();
    /**
     * Элемент меню "Файл" >> "Выйти из программы"
     */
    private JMenuItem menuItemExit = new JMenuItem();


    /**
     * Меню "Правка"
     */
    private JMenu menuEdit = new JMenu();
    /**
     * Элемент меню "Правка" >> "Отменить действие"
     */
    private JMenuItem menuItemUndo = new JMenuItem();
    /**
     * Элемент меню "Правка" >> "Повторить действие"
     */
    private JMenuItem menuItemRedo = new JMenuItem();
    /**
     * Элемент меню "Правка" >> "Вырезать"
     */
    private JMenuItem menuItemCut = new JMenuItem();
    /**
     * Элемент меню "Правка" >> "Копировать"
     */
    private JMenuItem menuItemCopy = new JMenuItem();
    /**
     * Элемент меню "Правка" >> "Вставить"
     */
    private JMenuItem menuItemPaste = new JMenuItem();
    /**
     * Элемент меню "Правка" >> "Удалить выделенные"
     */
    private JMenuItem menuItemRemoveSelected = new JMenuItem();
    /**
     * Элемент меню "Правка" >> "Выделить все"
     */
    private JMenuItem menuItemSelectAll = new JMenuItem();

    /**
     * Меню "Вид"
     */
    private JMenu menuView = new JMenu();
    /**
     * Меню "Вид" >> "Масштаб"
     */
    private JMenu menuScale = new JMenu();
    /**
     * Элемент меню "Вид" >> "Масштаб" >> "Показать весь чертеж"
     */
    private JMenuItem menuItemScaleReset = new JMenuItem();
    /**
     * Элемент меню "Вид" >> "Масштаб" >> "Показать весь чертеж"
     */
    private JMenuItem menuItemScalePlus = new JMenuItem();
    /**
     * Элемент меню "Вид" >> "Масштаб" >> "Показать весь чертеж"
     */
    private JMenuItem menuItemScaleMinus = new JMenuItem();
    /**
     * Элемент меню "Вид" >> "Параметры сетки"
     */
    private JMenuItem menuItemGridSettings = new JMenuItem();
    /**
     * Элемент меню "Вид" >> "Перестроить"
     */
    private JMenuItem menuItemRepaint = new JMenuItem();

    /**
     * Меню "Справка"
     */
    private JMenu menuInfo = new JMenu();
    /**
     * Элемент меню "Справка" >> "Горячие клавиши"
     */
    private JMenuItem menuItemHotKeys = new JMenuItem();
    /**
     * Элемент меню "Справка" >> "О программе"
     */
    private JMenuItem menuItemAboutProgram = new JMenuItem();

    /**
     * Окно "О программе"
     */
    private AboutProgramWindow aboutProgramWindow = new AboutProgramWindow();

    /**
     * Окно "Горячие клавиши"
     */
    private HotKeysWindow hotKeysWindow = new HotKeysWindow();


    public MenuBar() {
        initMenuFile();
        initMenuEdit();
        initMenuView();
        initMenuInfo();
    }

    /**
     * Инициализирует меню "Файл"
     */
    private void initMenuFile() {
        menuFile.setText("Файл");
        {
            {
                menuItemNew.setText("Новый");
                menuItemNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
                menuItemNew.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        FileData.newFile();
                        KeyListener.resetShiftCtrl();
                    }
                });
                menuFile.add(menuItemNew);
            }
        }

        menuFile.addSeparator();

        {
            {
                menuItemOpen.setText("Открыть...");
                menuItemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
                menuItemOpen.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        FileData.openFileChooser();
                        KeyListener.resetShiftCtrl();
                    }
                });
                menuFile.add(menuItemOpen);
            }
        }

        {
            {
                menuItemSaveAs.setText("Сохранить...");
                menuItemSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
                menuItemSaveAs.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        FileData.saveFileChooser();
                        KeyListener.resetShiftCtrl();
                    }
                });
                menuFile.add(menuItemSaveAs);
            }
        }

        menuFile.addSeparator();

        {
            {
                menuItemExit.setText("Выйти");
                menuItemExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));
                menuItemExit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Log.add("Выход из приложения");

                        Object[] options = {"Выйти", "Отмена"};
                        int n = JOptionPane
                                .showOptionDialog(getMainWindow(), "Вы уверены, что хотите выйти? Все несохраненные данные будут потеряны.",
                                        "Предупреждение", JOptionPane.YES_NO_OPTION,
                                        JOptionPane.QUESTION_MESSAGE, null, options,
                                        options[0]);
                        if (n == 0) {
                            System.exit(0);
                        }
                    }
                });
                menuFile.add(menuItemExit);
            }
        }
        add(menuFile);
    }

    /**
     * Инициализирует меню "Правка"
     */
    private void initMenuEdit() {
        menuEdit.setText("Правка");

        {
            {
                menuItemUndo.setText("Отменить");
                menuItemUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
                menuItemUndo.setEnabled(false);
                menuItemUndo.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        getStates().undo();
                        getDrawingArea().repaint();
                    }
                });
                menuEdit.add(menuItemUndo);
            }
        }

        {
            {
                menuItemRedo.setText("Повторить");
                menuItemRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
                menuItemRedo.setEnabled(false);
                menuItemRedo.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        getStates().redo();
                        getDrawingArea().repaint();
                    }
                });
                menuEdit.add(menuItemRedo);
            }
        }

        menuEdit.addSeparator();

        {
            {
                menuItemCut.setText("Вырезать");
                menuItemCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
                menuItemCut.setEnabled(false);
                menuItemCut.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Clipboard.cut();
                        getDrawingArea().repaint();
                    }
                });
                menuEdit.add(menuItemCut);
            }
        }

        {
            {
                menuItemCopy.setText("Копировать");
                menuItemCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
                menuItemCopy.setEnabled(false);
                menuItemCopy.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Clipboard.copy();
                        getDrawingArea().repaint();
                    }
                });
                menuEdit.add(menuItemCopy);
            }
        }

        {
            {
                menuItemPaste.setText("Вставить");
                menuItemPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
                menuItemPaste.setEnabled(false);
                menuItemPaste.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Clipboard.paste();
                    }
                });
                menuEdit.add(menuItemPaste);
            }
        }

        {
            {
                menuItemRemoveSelected.setText("Удалить");
                menuItemRemoveSelected.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
                menuItemRemoveSelected.setEnabled(false);
                menuItemRemoveSelected.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        getSelection().removeSelected();
                    }
                });
                menuEdit.add(menuItemRemoveSelected);
            }
        }

        menuEdit.addSeparator();

        {
            {
                menuItemSelectAll.setText("Выделить все");
                menuItemSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
                menuItemSelectAll.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        getSelection().selectAll();
                    }
                });
                menuEdit.add(menuItemSelectAll);
            }
        }
        add(menuEdit);
    }

    /**
     * Инициализирует меню "Вид"
     */
    private void initMenuView() {
        menuView.setText("Вид");

        {
            menuScale.setText("Масштаб");
            {
                {
                    {
                        menuItemScaleReset.setText("Показать все");
                        menuItemScaleReset.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD0, InputEvent.CTRL_MASK));
                        menuItemScaleReset.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                getScreen().initialize(getDrawingArea().getWidth(), getDrawingArea().getHeight());
                                getDrawingArea().repaint();
                            }
                        });
                        menuScale.add(menuItemScaleReset);
                    }
                }

                {
                    {
                        menuItemScalePlus.setText("Приблизить");
                        // http://bugs.java.com/bugdatabase/view_bug.do?bug_id=6942481
                        menuItemScalePlus.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ADD, InputEvent.CTRL_MASK));
                        menuItemScalePlus.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                getScreen().addScaleToCenter(1);
                            }
                        });
                        menuScale.add(menuItemScalePlus);
                    }
                }

                {
                    {
                        menuItemScaleMinus.setText("Отдалить");
                        menuItemScaleMinus.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, InputEvent.CTRL_MASK));
                        menuItemScaleMinus.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                getScreen().addScaleToCenter(-1);
                            }
                        });
                        menuScale.add(menuItemScaleMinus);
                    }
                }
            }
            menuView.add(menuScale);
        }

        menuView.addSeparator();

        {
            {
                menuItemRepaint.setText("Перестроить");
                menuItemRepaint.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
                menuItemRepaint.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        getDrawingArea().repaint();
                    }
                });
                menuView.add(menuItemRepaint);
            }
        }

        menuView.addSeparator();

        {
            {
                menuItemGridSettings.setText("Параметры сетки");
                menuItemGridSettings.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.ALT_MASK));
                menuItemGridSettings.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        getToolBar().getGridSettingsWindow().setVisible(true);
                    }
                });
                menuView.add(menuItemGridSettings);
            }
        }
        add(menuView);
    }

    /**
     * Инициализирует меню "Справка"
     */
    private void initMenuInfo() {
        menuInfo.setText("Справка");

        {
            {
                menuItemHotKeys.setText("Горячие клавиши");
                menuItemHotKeys.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.ALT_MASK));
                menuItemHotKeys.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        hotKeysWindow.setVisible(true);
                    }
                });
                menuInfo.add(menuItemHotKeys);
            }
        }

        //menuInfo.addSeparator();

        {
            {
                menuItemAboutProgram.setText("О программе");
                menuItemAboutProgram.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.ALT_MASK));
                menuItemAboutProgram.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        aboutProgramWindow.setVisible(true);
                    }
                });
                menuInfo.add(menuItemAboutProgram);
            }
        }
        add(menuInfo);
    }

    /**
     * Активирует/деактивирует кнопку "отменить"
     *
     * @param b true - активирует, false - деактивирует
     */
    public void setUndoEnabled(boolean b) {
        menuItemUndo.setEnabled(b);
    }

    /**
     * Активирует/деактивирует кнопку "повторить"
     *
     * @param b true - активирует, false - деактивирует
     */
    public void setRedoEnabled(boolean b) {
        menuItemRedo.setEnabled(b);
    }

    /**
     * Активирует/деактивирует кнопку "вырезать"
     *
     * @param b true - активирует, false - деактивирует
     */
    public void setCutEnabled(boolean b) {
        menuItemCut.setEnabled(b);
    }

    /**
     * Активирует/деактивирует кнопку "копировать"
     *
     * @param b true - активирует, false - деактивирует
     */
    public void setCopyEnabled(boolean b) {
        menuItemCopy.setEnabled(b);
    }

    /**
     * Активирует/деактивирует кнопку "вставить"
     *
     * @param b true - активирует, false - деактивирует
     */
    public void setPasteEnabled(boolean b) {
        menuItemPaste.setEnabled(b);
    }

    /**
     * Активирует/деактивирует кнопку "удалить"
     *
     * @param b true - активирует, false - деактивирует
     */
    public void setRemoveEnabled(boolean b) {
        menuItemRemoveSelected.setEnabled(b);
    }
}
