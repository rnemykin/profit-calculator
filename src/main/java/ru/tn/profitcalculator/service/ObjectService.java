package ru.tn.profitcalculator.service;

import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class ObjectService {

    @SneakyThrows
    public <T> T clone(T source) {
        Class<T> sourceClass = (Class<T>) Class.forName(source.getClass().getName());
        T target = sourceClass.newInstance();

        BeanUtils.copyProperties(source, target);
        return target;
    }

}
