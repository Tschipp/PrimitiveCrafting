package tschipp.primitivecrafting.common.helper;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.annotation.Nullable;

import net.minecraftforge.fml.relauncher.ReflectionHelper;

/**
 * Utility methods for reflection.
 *
 * @author Choonster
 */
public class ReflectionUtil
{
	/**
	 * Get a {@link MethodHandle} for a method.
	 *
	 * @param clazz
	 *            The class
	 * @param methodNames
	 *            The possible names of the method
	 * @param methodTypes
	 *            The argument types of the method
	 * @param <T>
	 *            The class
	 * @return The MethodHandle
	 */
	public static MethodHandle findMethod(final Class<?> clazz, final String methodName, @Nullable final String methodObfName, final Class<?>... parameterTypes)
	{
		final Method method = ReflectionHelper.findMethod(clazz, methodName, methodObfName, parameterTypes);
		try
		{
			return MethodHandles.lookup().unreflect(method);
		}
		catch (IllegalAccessException e)
		{
			throw new ReflectionHelper.UnableToFindMethodException(e);
		}
	}

	/**
	 * Get a {@link MethodHandle} for a field's getter.
	 *
	 * @param clazz
	 *            The class
	 * @param fieldNames
	 *            The possible names of the field
	 * @return The MethodHandle
	 */
	public static MethodHandle findFieldGetter(Class<?> clazz, String... fieldNames)
	{
		final Field field = ReflectionHelper.findField(clazz, fieldNames);

		try
		{
			return MethodHandles.lookup().unreflectGetter(field);
		}
		catch (IllegalAccessException e)
		{
			throw new ReflectionHelper.UnableToAccessFieldException(fieldNames, e);
		}
	}

	/**
	 * Get a {@link MethodHandle} for a field's setter.
	 *
	 * @param clazz
	 *            The class
	 * @param fieldNames
	 *            The possible names of the field
	 * @return The MethodHandle
	 */
	public static MethodHandle findFieldSetter(Class<?> clazz, String... fieldNames)
	{
		final Field field = ReflectionHelper.findField(clazz, fieldNames);

		try
		{
			return MethodHandles.lookup().unreflectSetter(field);
		}
		catch (IllegalAccessException e)
		{
			throw new ReflectionHelper.UnableToAccessFieldException(fieldNames, e);
		}
	}
}