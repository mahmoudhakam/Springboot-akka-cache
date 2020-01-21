package com.se.part.search.strategies;

import org.springframework.stereotype.Service;

@Service
public interface PartTransformationStrategy
{

	Object transformResult(Object parts, PartTransformationStrategy transformationStrategy);

}
