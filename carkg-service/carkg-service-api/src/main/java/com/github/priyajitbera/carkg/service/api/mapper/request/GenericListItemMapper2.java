package com.github.priyajitbera.carkg.service.api.mapper.request;

import org.apache.commons.lang3.function.TriConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class GenericListItemMapper2<SRC, TARGET, PARENT_CONTEXT, CONTEXT> {

    abstract TriConsumer<TARGET, SRC, CONTEXT> getMapper();

    protected List<TARGET> map(List<TARGET> existingItemList, List<SRC> sourceItems, PARENT_CONTEXT parentContext) {
        List<TARGET> existingList = Optional.ofNullable(existingItemList).orElse(new ArrayList<>());
        if (sourceItems == null || sourceItems.isEmpty()) {
            existingList.clear();
            return existingList;
        }

        List<TARGET> toRemove = existingList.stream().filter(
                existingTARGET -> sourceItems.stream()
                        .noneMatch(SRC -> match(existingTARGET, SRC))).toList();
        existingList.removeAll(toRemove);

        sourceItems.forEach(src -> {
            Optional<TARGET> existingOpt = existingList.stream().filter(target -> match(target, src)).findFirst();
            if (existingOpt.isPresent()) {
                getMapper().accept(existingOpt.get(), src, getContext(parentContext, existingOpt.get()));
            } else {
                TARGET newlyCreated = newTargetInstance();
                getMapper().accept(newlyCreated, src, getContext(parentContext, newlyCreated));
                existingList.add(newlyCreated);
            }
        });

        return existingList;
    }

    abstract CONTEXT getContext(PARENT_CONTEXT parentContext, TARGET target);

    abstract boolean match(TARGET TARGET, SRC SRC);

    abstract TARGET newTargetInstance();
}
