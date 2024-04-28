package org.fallt.mapper;

import org.fallt.dto.request.TrainingDto;
import org.fallt.model.Training;
import org.fallt.model.TrainingType;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TrainingMapper {

    TrainingMapper INSTANCE = Mappers.getMapper(TrainingMapper.class);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", source = "type", qualifiedByName = "trainingType")
    Training toEntity(TrainingDto request);

    @Mapping(target = "userName", expression = "java(training.getUser().getName())")
    @Mapping(target = "type", expression = "java(training.getType().getType())")
    TrainingDto toDtoResponse(Training training);

    List<TrainingDto> listEntityToListDto(List<Training> trainings);

    @Mapping(target = "type", source = "type", qualifiedByName = "trainingType")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTrainingFromDto(TrainingDto trainingDto, @MappingTarget Training training);

    @Named("trainingType")
    default TrainingType trainingType(String type) {
        TrainingType trainingType = new TrainingType();
        trainingType.setType(type);
        return trainingType;
    }
}
