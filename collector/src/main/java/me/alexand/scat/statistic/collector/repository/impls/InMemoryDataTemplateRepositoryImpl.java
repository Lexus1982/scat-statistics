package me.alexand.scat.statistic.collector.repository.impls;

import me.alexand.scat.statistic.collector.model.DataTemplate;
import me.alexand.scat.statistic.collector.model.TemplateType;
import me.alexand.scat.statistic.collector.repository.DataTemplateRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author asidorov84@gmail.com
 */

@Repository
public class InMemoryDataTemplateRepositoryImpl implements DataTemplateRepository {
    private final Map<TemplateType, DataTemplate> repository = new ConcurrentHashMap<>();

    @Override
    public List<DataTemplate> getAll() {
        return new ArrayList<>(repository.values());
    }

    @Override
    public DataTemplate save(DataTemplate template) {
        Objects.requireNonNull(template);
        repository.put(template.getType(), template);
        return template;
    }

    @Override
    public DataTemplate getByType(TemplateType type) {
        Objects.requireNonNull(type);
        return repository.get(type);
    }

    @Override
    public boolean delete(TemplateType type) {
        Objects.requireNonNull(type);
        return repository.remove(type) != null;
    }

    @Override
    public long getCount() {
        return repository.size();
    }

    @Override
    public void clear() {
        repository.clear();
    }
}