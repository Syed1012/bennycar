"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { useAuthStore } from "@/store/auth.store";
import { Loader2 } from "lucide-react";

const registerSchema = z.object({
  email: z.string().email("Invalid email address"),
  password: z.string().min(6, "Password must be at least 6 characters"),
  confirmPassword: z.string(),
  firstName: z.string().min(2, "First name must be at least 2 characters"),
  lastName: z.string().min(2, "Last name must be at least 2 characters"),
}).refine((data) => data.password === data.confirmPassword, {
  message: "Passwords don't match",
  path: ["confirmPassword"],
});

type RegisterFormData = z.infer<typeof registerSchema>;

export function RegisterForm() {
  const router = useRouter();
  const { register: registerUser, isLoading } = useAuthStore();
  const [error, setError] = useState<string | null>(null);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
  });

  const onSubmit = async (data: RegisterFormData) => {
    setError(null);
    try {
      await registerUser({
        email: data.email,
        password: data.password,
        firstName: data.firstName,
        lastName: data.lastName,
      });
      router.push("/vehicles");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to register");
    }
  };

  return (
    <Card className="w-full max-w-md border-2 border-[#e8d5c4] shadow-2xl">
      <CardHeader className="text-center space-y-2 pb-6">
        <CardTitle className="text-3xl font-bold bg-gradient-to-r from-[#c89968] to-[#d4a574] bg-clip-text text-transparent">
          Create Account
        </CardTitle>
        <CardDescription className="text-[#8b7355]">
          Join BennyCar to start your journey
        </CardDescription>
      </CardHeader>
      <form onSubmit={handleSubmit(onSubmit)}>
        <CardContent className="space-y-5">
          {error && (
            <div className="p-4 text-sm text-red-600 bg-red-50 border-2 border-red-200 rounded-lg">
              {error}
            </div>
          )}

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="firstName" className="text-[#4a3f35] font-semibold">First Name</Label>
              <Input
                id="firstName"
                placeholder="John"
                className="border-2 border-[#e8d5c4] focus:border-[#c89968] focus:ring-[#c89968] h-11"
                {...register("firstName")}
                disabled={isLoading}
              />
              {errors.firstName && (
                <p className="text-sm text-red-500">{errors.firstName.message}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="lastName" className="text-[#4a3f35] font-semibold">Last Name</Label>
              <Input
                id="lastName"
                placeholder="Doe"
                className="border-2 border-[#e8d5c4] focus:border-[#c89968] focus:ring-[#c89968] h-11"
                {...register("lastName")}
                disabled={isLoading}
              />
              {errors.lastName && (
                <p className="text-sm text-red-500">{errors.lastName.message}</p>
              )}
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="email" className="text-[#4a3f35] font-semibold">Email</Label>
            <Input
              id="email"
              type="email"
              placeholder="you@example.com"
              className="border-2 border-[#e8d5c4] focus:border-[#c89968] focus:ring-[#c89968] h-11"
              {...register("email")}
              disabled={isLoading}
            />
            {errors.email && (
              <p className="text-sm text-red-500">{errors.email.message}</p>
            )}
          </div>

          <div className="space-y-2">
            <Label htmlFor="password" className="text-[#4a3f35] font-semibold">Password</Label>
            <Input
              id="password"
              type="password"
              placeholder="••••••••"
              className="border-2 border-[#e8d5c4] focus:border-[#c89968] focus:ring-[#c89968] h-11"
              {...register("password")}
              disabled={isLoading}
            />
            {errors.password && (
              <p className="text-sm text-red-500">{errors.password.message}</p>
            )}
          </div>

          <div className="space-y-2">
            <Label htmlFor="confirmPassword" className="text-[#4a3f35] font-semibold">Confirm Password</Label>
            <Input
              id="confirmPassword"
              type="password"
              placeholder="••••••••"
              className="border-2 border-[#e8d5c4] focus:border-[#c89968] focus:ring-[#c89968] h-11"
              {...register("confirmPassword")}
              disabled={isLoading}
            />
            {errors.confirmPassword && (
              <p className="text-sm text-red-500">{errors.confirmPassword.message}</p>
            )}
          </div>
        </CardContent>

        <CardFooter className="flex flex-col space-y-4 pt-2">
          <Button 
            type="submit" 
            className="w-full h-11 bg-gradient-to-r from-[#c89968] to-[#d4a574] hover:opacity-90 text-white font-semibold" 
            disabled={isLoading}
          >
            {isLoading ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                Creating account...
              </>
            ) : (
              "Create Account"
            )}
          </Button>

          <p className="text-sm text-center text-[#8b7355]">
            Already have an account?{" "}
            <Link href="/auth/login" className="text-[#c89968] hover:text-[#d4a574] font-semibold underline">
              Sign in
            </Link>
          </p>
        </CardFooter>
      </form>
    </Card>
  );
}
