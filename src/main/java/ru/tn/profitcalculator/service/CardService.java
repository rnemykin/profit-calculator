package ru.tn.profitcalculator.service;

import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.singleton;

@Service
public class CardService {
    private static final Map<PosCategoryEnum, Set<BonusOptionEnum>> CATEGORY_2_OPTION;

    static {
        CATEGORY_2_OPTION = new HashMap<>();
        CATEGORY_2_OPTION.put(PosCategoryEnum.AUTO, singleton(BonusOptionEnum.AUTO));
        CATEGORY_2_OPTION.put(PosCategoryEnum.TRAVEL, singleton(BonusOptionEnum.TRAVEL));

        CATEGORY_2_OPTION.put(PosCategoryEnum.FUN, EnumSet.of(BonusOptionEnum.SAVING, BonusOptionEnum.CASH_BACK, BonusOptionEnum.COLLECTION, BonusOptionEnum.FUN));
        CATEGORY_2_OPTION.put(PosCategoryEnum.OTHER, EnumSet.of(BonusOptionEnum.SAVING, BonusOptionEnum.CASH_BACK, BonusOptionEnum.COLLECTION));
    }

    public List<BonusOptionEnum> getBonusOptionsByPosCategories(List<PosCategoryEnum> categories) {
        return categories.stream()
                .map(CATEGORY_2_OPTION::get)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

}
