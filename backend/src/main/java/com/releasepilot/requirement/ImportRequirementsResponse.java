package com.releasepilot.requirement;

import java.util.List;

public record ImportRequirementsResponse(int importedCount, List<String> skippedRows) {}
