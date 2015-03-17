package com.syzton.sunread.service.education_system;

import com.syzton.sunread.model.education_system.Clazz;
import javassist.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by Morgan-Leon on 2015/3/16.
 */
public interface ClazzService {

    public Clazz add(Clazz Clazz);

    public Clazz deleteById(Long id) throws NotFoundException;

    public Clazz update(Clazz updated) throws NotFoundException;

    public Clazz findById(Long id) throws NotFoundException;

    Page<Clazz> findAll(Pageable pageable) throws NotFoundException;
}
