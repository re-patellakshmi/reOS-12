if (APPLE)
    set(angle-common-platform-sources
        src/common/system_utils_posix.cpp
        src/common/system_utils_mac.cpp)
elseif (WIN32)
    set(angle-common-platform-sources
        src/common/system_utils_win.cpp
        src/common/system_utils_win32.cpp
        src/common/system_utils_winuwp)
else()
    set(angle-common-platform-sources
        src/common/system_utils_posix.cpp
        src/common/system_utils_linux.cpp)
endif()

add_library(
    angle_common
    src/common/Color.h
    src/common/Color.inc
    src/common/FastVector.h
    src/common/FixedVector.h
    src/common/Float16ToFloat32.cpp
    src/common/MemoryBuffer.cpp
    src/common/MemoryBuffer.h
    src/common/Optional.h
    src/common/PackedEGLEnums_autogen.cpp
    src/common/PackedEGLEnums_autogen.h
    src/common/PackedEnums.cpp
    src/common/PackedEnums.h
    src/common/PackedGLEnums_autogen.cpp
    src/common/PackedGLEnums_autogen.h
    src/common/PoolAlloc.cpp
    src/common/PoolAlloc.h
    src/common/aligned_memory.cpp
    src/common/aligned_memory.h
    src/common/android_util.cpp
    src/common/android_util.h
    src/common/angleutils.cpp
    src/common/angleutils.h
    src/common/apple_platform_utils.h
    src/common/bitset_utils.h
    src/common/debug.cpp
    src/common/debug.h
    src/common/event_tracer.cpp
    src/common/event_tracer.h
    src/common/hash_utils.h
    src/common/mathutil.cpp
    src/common/mathutil.h
    src/common/matrix_utils.cpp
    src/common/matrix_utils.h
    src/common/platform.h
    src/common/string_utils.cpp
    src/common/string_utils.h
    src/common/third_party/base/anglebase/base_export.h
    src/common/third_party/base/anglebase/containers/mru_cache.h
    src/common/third_party/base/anglebase/logging.h
    src/common/third_party/base/anglebase/macros.h
    src/common/third_party/base/anglebase/no_destructor.h
    src/common/third_party/base/anglebase/numerics/safe_conversions.h
    src/common/third_party/base/anglebase/numerics/safe_conversions_impl.h
    src/common/third_party/base/anglebase/numerics/safe_math.h
    src/common/third_party/base/anglebase/numerics/safe_math_impl.h
    src/common/third_party/base/anglebase/sha1.cc
    src/common/third_party/base/anglebase/sha1.h
    src/common/third_party/base/anglebase/sys_byteorder.h
    src/common/third_party/smhasher/src/PMurHash.cpp
    src/common/third_party/smhasher/src/PMurHash.h
    src/common/tls.cpp
    src/common/tls.h
    src/common/uniform_type_info_autogen.cpp
    src/common/utilities.cpp
    src/common/utilities.h
    src/common/vector_utils.h
    src/common/system_utils.h
    src/common/system_utils.cpp
    ${angle-common-platform-sources})
target_include_directories(
    angle_common
    PRIVATE
    include/
    src/
    src/common
    src/common/third_party/base
    src/common/third_party/base/anglebase
    PUBLIC
    src/common/third_party/base)

add_library(
    angle_shader_translator
    include/GLSLANG/ShaderLang.h
    include/GLSLANG/ShaderVars.h
    src/compiler/translator/blocklayout.h
    include/EGL/egl.h
    include/EGL/eglext.h
    include/EGL/eglplatform.h
    include/GLES2/gl2.h
    include/GLES2/gl2ext.h
    include/GLES2/gl2platform.h
    include/GLES3/gl3.h
    include/GLES3/gl31.h
    include/GLES3/gl32.h
    include/GLES3/gl3platform.h
    include/KHR/khrplatform.h
    include/angle_gl.h
    src/compiler/translator/BaseTypes.h
    src/compiler/translator/BuiltInFunctionEmulator.cpp
    src/compiler/translator/BuiltInFunctionEmulator.h
    src/compiler/translator/CallDAG.cpp
    src/compiler/translator/CallDAG.h
    src/compiler/translator/CodeGen.cpp
    src/compiler/translator/CollectVariables.cpp
    src/compiler/translator/CollectVariables.h
    src/compiler/translator/Common.h
    src/compiler/translator/Compiler.cpp
    src/compiler/translator/Compiler.h
    src/compiler/translator/ConstantUnion.cpp
    src/compiler/translator/ConstantUnion.h
    src/compiler/translator/Declarator.cpp
    src/compiler/translator/Declarator.h
    src/compiler/translator/Diagnostics.cpp
    src/compiler/translator/Diagnostics.h
    src/compiler/translator/DirectiveHandler.cpp
    src/compiler/translator/DirectiveHandler.h
    src/compiler/translator/ExtensionBehavior.cpp
    src/compiler/translator/ExtensionBehavior.h
    src/compiler/translator/FlagStd140Structs.cpp
    src/compiler/translator/FlagStd140Structs.h
    src/compiler/translator/FunctionLookup.cpp
    src/compiler/translator/FunctionLookup.h
    src/compiler/translator/HashNames.cpp
    src/compiler/translator/HashNames.h
    src/compiler/translator/ImmutableString.h
    src/compiler/translator/ImmutableStringBuilder.cpp
    src/compiler/translator/ImmutableStringBuilder.h
    src/compiler/translator/InfoSink.cpp
    src/compiler/translator/InfoSink.h
    src/compiler/translator/Initialize.cpp
    src/compiler/translator/Initialize.h
    src/compiler/translator/InitializeDll.cpp
    src/compiler/translator/InitializeDll.h
    src/compiler/translator/InitializeGlobals.h
    src/compiler/translator/IntermNode.cpp
    src/compiler/translator/IntermNode.h
    src/compiler/translator/IsASTDepthBelowLimit.cpp
    src/compiler/translator/IsASTDepthBelowLimit.h
    src/compiler/translator/Operator.cpp
    src/compiler/translator/Operator.h
    src/compiler/translator/OutputTree.cpp
    src/compiler/translator/OutputTree.h
    src/compiler/translator/ParseContext.cpp
    src/compiler/translator/ParseContext.h
    src/compiler/translator/ParseContext_ESSL_autogen.h
    src/compiler/translator/ParseContext_complete_autogen.h
    src/compiler/translator/ParseContext_interm.h
    src/compiler/translator/PoolAlloc.cpp
    src/compiler/translator/PoolAlloc.h
    src/compiler/translator/Pragma.h
    src/compiler/translator/QualifierTypes.cpp
    src/compiler/translator/QualifierTypes.h
    src/compiler/translator/Severity.h
    src/compiler/translator/ShaderLang.cpp
    src/compiler/translator/ShaderVars.cpp
    src/compiler/translator/StaticType.h
    src/compiler/translator/Symbol.cpp
    src/compiler/translator/Symbol.h
    src/compiler/translator/SymbolTable.cpp
    src/compiler/translator/SymbolTable.h
    src/compiler/translator/SymbolTable_autogen.h
    src/compiler/translator/SymbolUniqueId.cpp
    src/compiler/translator/SymbolUniqueId.h
    src/compiler/translator/Types.cpp
    src/compiler/translator/Types.h
    src/compiler/translator/ValidateAST.cpp
    src/compiler/translator/ValidateAST.h
    src/compiler/translator/ValidateGlobalInitializer.cpp
    src/compiler/translator/ValidateGlobalInitializer.h
    src/compiler/translator/ValidateLimitations.cpp
    src/compiler/translator/ValidateLimitations.h
    src/compiler/translator/ValidateMaxParameters.cpp
    src/compiler/translator/ValidateMaxParameters.h
    src/compiler/translator/ValidateOutputs.cpp
    src/compiler/translator/ValidateOutputs.h
    src/compiler/translator/ValidateSwitch.cpp
    src/compiler/translator/ValidateSwitch.h
    src/compiler/translator/ValidateVaryingLocations.cpp
    src/compiler/translator/ValidateVaryingLocations.h
    src/compiler/translator/VariablePacker.cpp
    src/compiler/translator/VariablePacker.h
    src/compiler/translator/blocklayout.cpp
    src/compiler/translator/glslang.h
    src/compiler/translator/glslang_lex_autogen.cpp
    src/compiler/translator/glslang_tab_autogen.cpp
    src/compiler/translator/glslang_tab_autogen.h
    src/compiler/translator/length_limits.h
    src/compiler/translator/tree_ops/AddAndTrueToLoopCondition.cpp
    src/compiler/translator/tree_ops/AddAndTrueToLoopCondition.h
    src/compiler/translator/tree_ops/BreakVariableAliasingInInnerLoops.cpp
    src/compiler/translator/tree_ops/BreakVariableAliasingInInnerLoops.h
    src/compiler/translator/tree_ops/ClampFragDepth.cpp
    src/compiler/translator/tree_ops/ClampFragDepth.h
    src/compiler/translator/tree_ops/ClampPointSize.cpp
    src/compiler/translator/tree_ops/ClampPointSize.h
    src/compiler/translator/tree_ops/DeclareAndInitBuiltinsForInstancedMultiview.cpp
    src/compiler/translator/tree_ops/DeclareAndInitBuiltinsForInstancedMultiview.h
    src/compiler/translator/tree_ops/DeferGlobalInitializers.cpp
    src/compiler/translator/tree_ops/DeferGlobalInitializers.h
    src/compiler/translator/tree_ops/EarlyFragmentTestsOptimization.cpp
    src/compiler/translator/tree_ops/EarlyFragmentTestsOptimization.h
    src/compiler/translator/tree_ops/EmulateGLFragColorBroadcast.cpp
    src/compiler/translator/tree_ops/EmulateGLFragColorBroadcast.h
    src/compiler/translator/tree_ops/EmulateMultiDrawShaderBuiltins.cpp
    src/compiler/translator/tree_ops/EmulateMultiDrawShaderBuiltins.h
    src/compiler/translator/tree_ops/EmulatePrecision.cpp
    src/compiler/translator/tree_ops/EmulatePrecision.h
    src/compiler/translator/tree_ops/ExpandIntegerPowExpressions.cpp
    src/compiler/translator/tree_ops/ExpandIntegerPowExpressions.h
    src/compiler/translator/tree_ops/FoldExpressions.cpp
    src/compiler/translator/tree_ops/FoldExpressions.h
    src/compiler/translator/tree_ops/InitializeVariables.cpp
    src/compiler/translator/tree_ops/InitializeVariables.h
    src/compiler/translator/tree_ops/NameEmbeddedUniformStructs.cpp
    src/compiler/translator/tree_ops/NameEmbeddedUniformStructs.h
    src/compiler/translator/tree_ops/PruneEmptyCases.cpp
    src/compiler/translator/tree_ops/PruneEmptyCases.h
    src/compiler/translator/tree_ops/PruneNoOps.cpp
    src/compiler/translator/tree_ops/PruneNoOps.h
    src/compiler/translator/tree_ops/RecordConstantPrecision.cpp
    src/compiler/translator/tree_ops/RecordConstantPrecision.h
    src/compiler/translator/tree_ops/RegenerateStructNames.cpp
    src/compiler/translator/tree_ops/RegenerateStructNames.h
    src/compiler/translator/tree_ops/RemoveArrayLengthMethod.cpp
    src/compiler/translator/tree_ops/RemoveArrayLengthMethod.h
    src/compiler/translator/tree_ops/RemoveAtomicCounterBuiltins.cpp
    src/compiler/translator/tree_ops/RemoveAtomicCounterBuiltins.h
    src/compiler/translator/tree_ops/RemoveDynamicIndexing.cpp
    src/compiler/translator/tree_ops/RemoveDynamicIndexing.h
    src/compiler/translator/tree_ops/RemoveInactiveInterfaceVariables.cpp
    src/compiler/translator/tree_ops/RemoveInactiveInterfaceVariables.h
    src/compiler/translator/tree_ops/RemoveInvariantDeclaration.cpp
    src/compiler/translator/tree_ops/RemoveInvariantDeclaration.h
    src/compiler/translator/tree_ops/RemovePow.cpp
    src/compiler/translator/tree_ops/RemovePow.h
    src/compiler/translator/tree_ops/RemoveUnreferencedVariables.cpp
    src/compiler/translator/tree_ops/RemoveUnreferencedVariables.h
    src/compiler/translator/tree_ops/RewriteAtomicCounters.cpp
    src/compiler/translator/tree_ops/RewriteAtomicCounters.h
    src/compiler/translator/tree_ops/RewriteAtomicFunctionExpressions.cpp
    src/compiler/translator/tree_ops/RewriteAtomicFunctionExpressions.h
    src/compiler/translator/tree_ops/RewriteCubeMapSamplersAs2DArray.cpp
    src/compiler/translator/tree_ops/RewriteCubeMapSamplersAs2DArray.h
    src/compiler/translator/tree_ops/RewriteDfdy.cpp
    src/compiler/translator/tree_ops/RewriteDfdy.h
    src/compiler/translator/tree_ops/RewriteDoWhile.cpp
    src/compiler/translator/tree_ops/RewriteDoWhile.h
    src/compiler/translator/tree_ops/RewriteExpressionsWithShaderStorageBlock.cpp
    src/compiler/translator/tree_ops/RewriteExpressionsWithShaderStorageBlock.h
    src/compiler/translator/tree_ops/RewriteRepeatedAssignToSwizzled.cpp
    src/compiler/translator/tree_ops/RewriteRepeatedAssignToSwizzled.h
    src/compiler/translator/tree_ops/RewriteRowMajorMatrices.cpp
    src/compiler/translator/tree_ops/RewriteRowMajorMatrices.h
    src/compiler/translator/tree_ops/RewriteStructSamplers.cpp
    src/compiler/translator/tree_ops/RewriteStructSamplers.h
    src/compiler/translator/tree_ops/RewriteStructSamplersOld.cpp
    src/compiler/translator/tree_ops/RewriteTexelFetchOffset.cpp
    src/compiler/translator/tree_ops/RewriteTexelFetchOffset.h
    src/compiler/translator/tree_ops/RewriteUnaryMinusOperatorFloat.cpp
    src/compiler/translator/tree_ops/RewriteUnaryMinusOperatorFloat.h
    src/compiler/translator/tree_ops/RewriteUnaryMinusOperatorInt.cpp
    src/compiler/translator/tree_ops/RewriteUnaryMinusOperatorInt.h
    src/compiler/translator/tree_ops/ScalarizeVecAndMatConstructorArgs.cpp
    src/compiler/translator/tree_ops/ScalarizeVecAndMatConstructorArgs.h
    src/compiler/translator/tree_ops/SeparateDeclarations.cpp
    src/compiler/translator/tree_ops/SeparateDeclarations.h
    src/compiler/translator/tree_ops/SimplifyLoopConditions.cpp
    src/compiler/translator/tree_ops/SimplifyLoopConditions.h
    src/compiler/translator/tree_ops/SplitSequenceOperator.cpp
    src/compiler/translator/tree_ops/SplitSequenceOperator.h
    src/compiler/translator/tree_ops/UnfoldShortCircuitAST.cpp
    src/compiler/translator/tree_ops/UnfoldShortCircuitAST.h
    src/compiler/translator/tree_ops/UseInterfaceBlockFields.cpp
    src/compiler/translator/tree_ops/UseInterfaceBlockFields.h
    src/compiler/translator/tree_ops/VectorizeVectorScalarArithmetic.cpp
    src/compiler/translator/tree_ops/VectorizeVectorScalarArithmetic.h
    src/compiler/translator/tree_util/BuiltIn.h
    src/compiler/translator/tree_util/BuiltIn_ESSL_autogen.h
    src/compiler/translator/tree_util/BuiltIn_complete_autogen.h
    src/compiler/translator/tree_util/FindFunction.cpp
    src/compiler/translator/tree_util/FindFunction.h
    src/compiler/translator/tree_util/FindMain.cpp
    src/compiler/translator/tree_util/FindMain.h
    src/compiler/translator/tree_util/FindSymbolNode.cpp
    src/compiler/translator/tree_util/FindSymbolNode.h
    src/compiler/translator/tree_util/IntermNodePatternMatcher.cpp
    src/compiler/translator/tree_util/IntermNodePatternMatcher.h
    src/compiler/translator/tree_util/IntermNode_util.cpp
    src/compiler/translator/tree_util/IntermNode_util.h
    src/compiler/translator/tree_util/IntermTraverse.cpp
    src/compiler/translator/tree_util/IntermTraverse.h
    src/compiler/translator/tree_util/NodeSearch.h
    src/compiler/translator/tree_util/ReplaceClipDistanceVariable.cpp
    src/compiler/translator/tree_util/ReplaceClipDistanceVariable.h
    src/compiler/translator/tree_util/ReplaceShadowingVariables.cpp
    src/compiler/translator/tree_util/ReplaceShadowingVariables.h
    src/compiler/translator/tree_util/ReplaceVariable.cpp
    src/compiler/translator/tree_util/ReplaceVariable.h
    src/compiler/translator/tree_util/RunAtTheEndOfShader.cpp
    src/compiler/translator/tree_util/RunAtTheEndOfShader.h
    src/compiler/translator/tree_util/Visit.h
    src/compiler/translator/util.cpp
    src/compiler/translator/util.h
    src/third_party/compiler/ArrayBoundsClamper.cpp
    src/third_party/compiler/ArrayBoundsClamper.h
    src/compiler/translator/OutputESSL.cpp
    src/compiler/translator/OutputESSL.h
    src/compiler/translator/TranslatorESSL.cpp
    src/compiler/translator/TranslatorESSL.h
    src/compiler/translator/BuiltInFunctionEmulatorGLSL.cpp
    src/compiler/translator/BuiltInFunctionEmulatorGLSL.h
    src/compiler/translator/BuiltinsWorkaroundGLSL.cpp
    src/compiler/translator/BuiltinsWorkaroundGLSL.h
    src/compiler/translator/ExtensionGLSL.cpp
    src/compiler/translator/ExtensionGLSL.h
    src/compiler/translator/OutputGLSL.cpp
    src/compiler/translator/OutputGLSL.h
    src/compiler/translator/OutputGLSLBase.cpp
    src/compiler/translator/OutputGLSLBase.h
    src/compiler/translator/TranslatorGLSL.cpp
    src/compiler/translator/TranslatorGLSL.h
    src/compiler/translator/VersionGLSL.cpp
    src/compiler/translator/VersionGLSL.h
    src/compiler/translator/emulated_builtin_functions_hlsl_autogen.cpp
    src/compiler/translator/tree_ops/AddDefaultReturnStatements.cpp
    src/compiler/translator/tree_ops/AddDefaultReturnStatements.h
    src/compiler/translator/tree_ops/ArrayReturnValueToOutParameter.cpp
    src/compiler/translator/tree_ops/ArrayReturnValueToOutParameter.h
    src/compiler/translator/tree_ops/RemoveSwitchFallThrough.cpp
    src/compiler/translator/tree_ops/RemoveSwitchFallThrough.h
    src/compiler/translator/tree_ops/RewriteElseBlocks.cpp
    src/compiler/translator/tree_ops/RewriteElseBlocks.h
    src/compiler/translator/tree_ops/SeparateArrayConstructorStatements.cpp
    src/compiler/translator/tree_ops/SeparateArrayConstructorStatements.h
    src/compiler/translator/tree_ops/SeparateArrayInitialization.cpp
    src/compiler/translator/tree_ops/SeparateArrayInitialization.h
    src/compiler/translator/tree_ops/SeparateExpressionsReturningArrays.cpp
    src/compiler/translator/tree_ops/SeparateExpressionsReturningArrays.h
    src/compiler/translator/tree_ops/UnfoldShortCircuitToIf.cpp
    src/compiler/translator/tree_ops/UnfoldShortCircuitToIf.h
    src/compiler/translator/tree_ops/WrapSwitchStatementsInBlocks.cpp
    src/compiler/translator/tree_ops/WrapSwitchStatementsInBlocks.h
    src/compiler/translator/OutputVulkanGLSL.cpp
    src/compiler/translator/OutputVulkanGLSL.h
    src/compiler/translator/TranslatorVulkan.cpp
    src/compiler/translator/TranslatorVulkan.h
    src/compiler/translator/ImmutableString_autogen.cpp
    src/compiler/translator/SymbolTable_autogen.cpp
    src/compiler/preprocessor/DiagnosticsBase.cpp
    src/compiler/preprocessor/DiagnosticsBase.h
    src/compiler/preprocessor/DirectiveHandlerBase.cpp
    src/compiler/preprocessor/DirectiveHandlerBase.h
    src/compiler/preprocessor/DirectiveParser.cpp
    src/compiler/preprocessor/DirectiveParser.h
    src/compiler/preprocessor/ExpressionParser.h
    src/compiler/preprocessor/Input.cpp
    src/compiler/preprocessor/Input.h
    src/compiler/preprocessor/Lexer.cpp
    src/compiler/preprocessor/Lexer.h
    src/compiler/preprocessor/Macro.cpp
    src/compiler/preprocessor/Macro.h
    src/compiler/preprocessor/MacroExpander.cpp
    src/compiler/preprocessor/MacroExpander.h
    src/compiler/preprocessor/Preprocessor.cpp
    src/compiler/preprocessor/Preprocessor.h
    src/compiler/preprocessor/SourceLocation.h
    src/compiler/preprocessor/Token.cpp
    src/compiler/preprocessor/Token.h
    src/compiler/preprocessor/Tokenizer.h
    src/compiler/preprocessor/numeric_lex.h
    src/compiler/preprocessor/preprocessor_lex_autogen.cpp
    src/compiler/preprocessor/preprocessor_tab_autogen.cpp
    src/libShaderTranslator/libShaderTranslator.cpp)
target_include_directories(
    angle_shader_translator
    PRIVATE
    include/
    include/GLSLANG
    src/
    src/compiler
    src/compiler/translator
    src/compiler/translator/tree_ops
    src/compiler/translator/tree_util
    src/compiler/preprocessor
    src/third_party/compiler
    PUBLIC
    src/libShaderTranslator)
target_compile_definitions(
    angle_shader_translator
    PRIVATE
    ANGLE_ENABLE_GLSL=1
    ANGLE_ENABLE_ESSL=1)
target_link_libraries(
    angle_shader_translator
    PRIVATE
    angle_common)
