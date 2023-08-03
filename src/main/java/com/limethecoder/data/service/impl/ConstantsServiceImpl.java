package com.limethecoder.data.service.impl;


import com.limethecoder.data.domain.Constants;
import com.limethecoder.data.repository.ConstantsRepository;
import com.limethecoder.data.service.ConstantsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConstantsServiceImpl implements ConstantsService {
    @Autowired
    private ConstantsRepository constantsRepository;

    @Override
    public Constants getInstance() {
        return constantsRepository.findAll().get(0);
    }

    @Override
    public Constants addConstant(String element, String collection) {
        Constants constants = getInstance();

        if(constants != null) {
            if (collection.equals(ConstantsService.GENRE_TYPES) &&
                    constants.getGenres() != null) {
                constants.getGenres().add(element);
            }

            constantsRepository.save(constants);
        }

        return constants;
    }

    @Override
    public void deleteConstant(String element, String collection) {
        Constants constants = getInstance();

        if(constants != null) {
            if (collection.equals(ConstantsService.GENRE_TYPES) &&
                    constants.getGenres() != null) {
                constants.getGenres().remove(element);
            }

            constantsRepository.save(constants);
        }
    }

    @Override
    public boolean isExistsConstant(String element, String collection) {
        Constants constants = getInstance();

        if(constants != null) {
            if (collection.equals(ConstantsService.GENRE_TYPES) &&
                    constants.getGenres() != null) {
                return constants.getGenres().contains(element);
            }
        }

        return false;
    }

    @Override
    public long getCnt(String collection) {
        Constants constants = getInstance();

        if(constants != null) {
            if (collection.equals(ConstantsService.GENRE_TYPES) &&
                    constants.getGenres() != null) {
                return constants.getGenres().size();
            }
        }

        return 0;
    }

    @Override
    public List<String> getConstantsByType(String type) {
        Constants constants = getInstance();

        if(constants != null) {
            if (type.equals(ConstantsService.GENRE_TYPES) &&
                    constants.getGenres() != null) {
                return constants.getGenres();
            }
        }

        return new ArrayList<>();
    }
}
