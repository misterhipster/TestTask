package Logic;

import Logic.DataManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

public class FileManager {
    private DataManager dataManager;

    public FileManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public void loadFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            Gson gson = new Gson();
            Type sectionListType = new TypeToken<List<Section>>() {
            }.getType();
            List<Section> sections = gson.fromJson(reader, sectionListType);
            // очищаем текущие секции
            dataManager.getSections().clear();
            // добавляем загруженные секции
            dataManager.getSections().addAll(sections);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // метод для сохранения разделов (секций) в JSON файл
    public void saveFile(String filename) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create(); // Создаем Gson с форматированием
        String json = gson.toJson(dataManager.getSections()); // Сериализуем список секций в JSON
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // записываем JSON в файл
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DataManager getDataManager() {
        return dataManager;
    }
}
