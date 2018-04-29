package me.alexand.scat.statistic.collector.repository.impls;

import me.alexand.scat.statistic.collector.model.InfoModelEntity;
import me.alexand.scat.statistic.collector.repository.InfoModelRepository;
import me.alexand.scat.statistic.collector.utils.exceptions.UnknownInfoModelException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Хранилище InfoModelEntity
 *
 * @author asidorov84@gmail.com
 */

@Repository
public class InMemoryInfoModelRepositoryImpl implements InfoModelRepository {
    private final Map<Integer, InfoModelEntity> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(1);

    @Override
    public InfoModelEntity save(InfoModelEntity entity) {
        Objects.requireNonNull(entity);

        if (entity.getId() == 0) {
            entity.setId(counter.getAndIncrement());
        }

        repository.put(entity.getId(), entity);

        return entity;
    }

    @Override
    public boolean delete(int id) {
        return repository.remove(id) != null;
    }

    @Override
    public List<InfoModelEntity> getAll() {
        return new ArrayList<>(repository.values());
    }

    @Override
    public InfoModelEntity getById(int id) {
        return repository.get(id);
    }

    @Override
    public InfoModelEntity getByEnterpriseNumberAndInformationElementIdentifier(
            long enterpriseNumber,
            int informationElementId) throws UnknownInfoModelException {
        return repository.values().stream()
                .filter(entity -> entity.getEnterpriseNumber() == enterpriseNumber &&
                        entity.getInformationElementId() == informationElementId)
                .findFirst()
                .orElseThrow(() -> new UnknownInfoModelException(
                        String.format("unknown info model with enterprise number = %d and identifier = %d",
                                enterpriseNumber,
                                informationElementId)));
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
