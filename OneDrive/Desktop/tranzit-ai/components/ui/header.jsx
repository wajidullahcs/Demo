import Image from "next/image";
import Link from "next/link";
import { SignedIn, SignedOut, SignInButton, UserButton } from "@clerk/nextjs";
import { ChevronDown, FileText, GraduationCap, LayoutDashboard, PenBox } from "lucide-react";
import { Button } from "./button";
import { DropdownMenu, DropdownMenuContent, DropdownMenuGroup, DropdownMenuItem, DropdownMenuLabel, DropdownMenuSeparator, DropdownMenuTrigger } from "./dropdown-menu";

const Header = () => {
  return (
    <header className="fixed top-0 w-full border-b bg-background/80 backdrop-blur-md z-50 supports-[backdrop-filter]:bg-background/60">
      
      <nav className="container mx-auto px-4 h-16 flex items-center justify-between">

        {/* ✅ LEFT SIDE — LOGO */}
        <Link href="/">
          <Image
            src="/logo1.png"
            alt="TranzitAi logo"
            width={200}
            height={60}
            priority
            className="h-17 py-1 w-auto object-contain"
          />
        </Link>

        {/* ✅ RIGHT SIDE — ALL BUTTONS */}
        <div className="flex items-center space-x-2 md:spacex4">

          {/* Dashboard Button (Only when Signed In) */}
          <SignedIn>
            <Link href="/dashboard">
              <Button
                variant="outline"
                className="hidden md:inline-flex items-center gap-2"
              >
                <LayoutDashboard className="h-4 w-4" />
              <span className="hidden md:block">  Industry Insights </span>
              </Button>
            </Link>
      


<DropdownMenu>
  <DropdownMenuTrigger>

 <Button
                className="hidden md:inline-flex items-center gap-2">
                <LayoutDashboard className="h-4 w-4" />
              <span className="hidden md:block"> Growth Tools </span>
                <ChevronDown className="h-4 w-4" />
              </Button>
  </DropdownMenuTrigger>
  <DropdownMenuContent>
  <DropdownMenuItem>

<Link href="/TranzitAi-resume" className="flex items-center gap-2">
 <FileText className="h-4 w-4" />
 <span>Build Resuma</span>
</Link>

  </DropdownMenuItem>
  <DropdownMenuItem>

 <Link
                    href="/TranzitAi-Cover-letter" className="flex items-center gap-2">
                    <PenBox className="h-4 w-4" />
                    Cover Letter
                  </Link>

  </DropdownMenuItem>
  <DropdownMenuItem>

 <Link
                    href="/interview" className="flex items-center gap-2">
                    <GraduationCap className="h-4 w-4" />
                    Interview preparation
                  </Link>

  </DropdownMenuItem>
  </DropdownMenuContent>
</DropdownMenu>

    </SignedIn>

          {/* Sign In Button */}
          {/* Sign In Button */}
<SignedOut>
  <SignInButton mode="modal">
    <Button variant="outline">Sign In</Button>
  </SignInButton>
</SignedOut>

          {/* User Profile */}
          <SignedIn>
            <UserButton 
            appearance={{
                elements: {
                  avatarBox: "w-10 h-10",
                  userButtonPopoverCard: "shadow-xl",
                  userPreviewMainIdentifier: "font-semibold",
                },
              }}
              afterSignOutUrl="/"
            />
          </SignedIn>

        </div>
      </nav>
    </header>
  );
};

export default Header;