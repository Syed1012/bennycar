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

const loginSchema = z.object({
  email: z.string().email("Invalid email address"),
  password: z.string().min(6, "Password must be at least 6 characters"),
});

type LoginFormData = z.infer<typeof loginSchema>;

export function LoginForm() {
  const router = useRouter();
  const { login, isLoading } = useAuthStore();
  const [error, setError] = useState<string | null>(null);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = async (data: LoginFormData) => {
    setError(null);
    try {
      await login(data.email, data.password);
      router.push("/");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to login");
    }
  };

  return (
    <Card className="w-full max-w-md border-2 border-[#e8d5c4] shadow-2xl">
      <CardHeader className="text-center space-y-2 pb-6">
        <CardTitle className="text-3xl font-bold bg-gradient-to-r from-[#c89968] to-[#d4a574] bg-clip-text text-transparent">
          Welcome Back
        </CardTitle>
        <CardDescription className="text-[#8b7355]">
          Sign in to your BennyCar account
        </CardDescription>
      </CardHeader>
      <form onSubmit={handleSubmit(onSubmit)}>
        <CardContent className="space-y-5">
          {error && (
            <div className="p-4 text-sm text-red-600 bg-red-50 border-2 border-red-200 rounded-lg">
              {error}
            </div>
          )}
          
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
                Signing in...
              </>
            ) : (
              "Sign In"
            )}
          </Button>

          <p className="text-sm text-center text-[#8b7355]">
            Don&apos;t have an account?{" "}
            <Link href="/auth/register" className="text-[#c89968] hover:text-[#d4a574] font-semibold underline">
              Sign up
            </Link>
          </p>
        </CardFooter>
      </form>
    </Card>
  );
}
