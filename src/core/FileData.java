package core;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;

import static core.DrawingArea.*;
import static ui.MainWindow.getDrawingArea;
import static ui.MainWindow.getMainWindow;

/**
 * Работа с файлами
 */
public class FileData {

    /**
     * Считывает данные из файла и представляет их в виде строки
     *
     * @param filePath путь до файла
     * @return данные из файла в виде строки
     * @throws IOException исключение ввода/вывода
     */
    public static String readFileAsString(String filePath) throws IOException {
        StringBuffer fileData = new StringBuffer();
        BufferedReader reader = new BufferedReader(
                new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }

    /**
     * Устанавливает русские заголовки для компонента "выбор файла"
     *
     * @param fc компонент выбора файла
     * @link http://www.java2s.com/Tutorial/Java/0240__Swing/CustomizingaJFileChooserLookandFeel.htm
     */
    public static void setRuLocale(JFileChooser fc) {
        UIManager.put("FileChooser.openDialogTitleText", "Открыть файл");
        UIManager.put("FileChooser.lookInLabelText", "Расположение");
        UIManager.put("FileChooser.openButtonText", "Открыть");
        UIManager.put("FileChooser.cancelButtonText", "Отмена");
        UIManager.put("FileChooser.fileNameLabelText", "Имя файла");
        UIManager.put("FileChooser.filesOfTypeLabelText", "Типы файлов");
        UIManager.put("FileChooser.openButtonToolTipText", "Открыть выбранный файл");
        UIManager.put("FileChooser.cancelButtonToolTipText", "Отмена");
        UIManager.put("FileChooser.fileNameHeaderText", "Имя файла");
        UIManager.put("FileChooser.upFolderToolTipText", "Вверх");
        UIManager.put("FileChooser.homeFolderToolTipText", "Рабочий стол");
        UIManager.put("FileChooser.newFolderToolTipText", "Новая папка");
        UIManager.put("FileChooser.listViewButtonToolTipText", "Списком");
        UIManager.put("FileChooser.newFolderButtonText", "Новая папка");
        UIManager.put("FileChooser.renameFileButtonText", "Переименовать");
        UIManager.put("FileChooser.deleteFileButtonText", "Удалить");
        UIManager.put("FileChooser.filterLabelText", "Тип файлов");
        UIManager.put("FileChooser.detailsViewButtonToolTipText", "Детали");
        UIManager.put("FileChooser.fileSizeHeaderText", "Размер");
        UIManager.put("FileChooser.fileDateHeaderText", "Дата изменения");
        UIManager.put("FileChooser.directoryOpenButtonText", "Открыть");
        UIManager.put("FileChooser.directoryOpenButtonToolTipText", "Открыть выделенную директорию");
        UIManager.put("FileChooser.newFolderButtonText", "Новая папка");
        SwingUtilities.updateComponentTreeUI(fc);
    }

    /**
     * Создает новый чертеж
     */
    public static void newFile() {
        Object[] options = {"Создать", "Отмена"};
        int n = JOptionPane
                .showOptionDialog(getMainWindow(), "Вы уверены, что хотите создать новый чертеж? Все несохраненные данные будут потеряны.",
                        "Предупреждение", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options,
                        options[0]);
        if (n == 0) {
            getData().removeAllFigures();
            getData().removeAllMeters();
            getData().removeAllGroups();
            getDrawingArea().getMorpher().removeAll();
            getStates().resetAll();
            getStates().fixState();
            getDrawingArea().repaint();
        }
    }

    /**
     * Открывает диалог выбора файла
     */
    public static void openFileChooser() {
        JFileChooser fc = new JFileChooser();
        FileData.setRuLocale(fc);
        //fc.setCurrentDirectory(new File("C:/"));
        fc.addChoosableFileFilter(new JSONFilter());
        fc.setAcceptAllFileFilterUsed(false);
        if (fc.showDialog(null, "Открыть") == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try {
                String json = FileData.readFileAsString(file.toString());
                getData().removeAllFigures();
                getData().removeAllMeters();
                getData().removeAllGroups();
                getDrawingArea().getMorpher().removeAll();
                getStates().resetAll();

                getData().loadJson(json);
                getStates().fixState();
                getScreen().initialize(getDrawingArea().getWidth(), getDrawingArea().getHeight());
                setToolMode(ToolMode.CURSOR);
            } catch (IOException e1) {
                Log.add("Ошибка при открытии JSON!");
                e1.printStackTrace();
            }
        }
    }

    /**
     * Открывает диалог сохранения файла
     */
    public static void saveFileChooser() {
        JFileChooser fc = new JFileChooser();
        FileData.setRuLocale(fc);
        fc.setSelectedFile(new File("Без названия.json"));
        fc.addChoosableFileFilter(new JSONFilter());
        fc.setAcceptAllFileFilterUsed(false);
        if (fc.showDialog(null, "Сохранить") == JFileChooser.APPROVE_OPTION) {
            FileWriter fw = null;
            try {
                fw = new FileWriter(fc.getSelectedFile());
                fw.write(getData().getJson().toJSONString());
                fw.close();
            } catch (IOException e1) {
                Log.add("Ошибка при сохранении JSON!");
                e1.printStackTrace();
            }
        }
    }

    /**
     * JSON фильтр
     */
    public static class JSONFilter extends FileFilter {

        /**
         * Проверяет, подходит ли заданный файл под текущий фильтр
         *
         * @param f файл
         * @return true - подходит, false - не подходит
         */
        @Override
        public boolean accept(File f) {
            if (f.isDirectory())
                return true;

            // получение и проверка расширения файла
            String s = f.getName();
            int i = s.lastIndexOf('.');
            if (i > 0 && i < s.length() - 1)
                return s.substring(i + 1).toLowerCase().equals("json");

            return false;
        }

        /**
         * Возвращает описание фильтра
         *
         * @return описание фильтра
         */
        @Override
        public String getDescription() {
            return "Файлы JSON (*.json)";
        }
    }
}
