# BennyCar FrontendThis is a [Next.js](https://nextjs.org) project bootstrapped with [`create-next-app`](https://nextjs.org/docs/app/api-reference/cli/create-next-app).



Modern, responsive frontend for the BennyCar vehicle purchasing platform built with **Next.js 14**, **React**, **TypeScript**, and **Tailwind CSS**.## Getting Started



## 🚀 Tech StackFirst, run the development server:



- **Framework**: Next.js 14 (App Router)```bash

- **Language**: TypeScriptnpm run dev

- **Styling**: Tailwind CSS# or

- **State Management**: Zustandyarn dev

- **Form Handling**: React Hook Form + Zod# or

- **HTTP Client**: Axiospnpm dev

- **UI Components**: Radix UI + Custom Components# or

- **Icons**: Lucide Reactbun dev

```

## 📁 Project Structure

Open [http://localhost:3000](http://localhost:3000) with your browser to see the result.

```

frontend/You can start editing the page by modifying `app/page.tsx`. The page auto-updates as you edit the file.

├── app/                      # Next.js App Router

│   ├── auth/                # Authentication pagesThis project uses [`next/font`](https://nextjs.org/docs/app/building-your-application/optimizing/fonts) to automatically optimize and load [Geist](https://vercel.com/font), a new font family for Vercel.

│   │   ├── login/          

│   │   └── register/       ## Learn More

│   ├── vehicles/           # Vehicle catalog (placeholder)

│   ├── orders/             # Order management (placeholder)To learn more about Next.js, take a look at the following resources:

│   ├── layout.tsx          # Root layout with header/footer

│   └── page.tsx            # Landing page- [Next.js Documentation](https://nextjs.org/docs) - learn about Next.js features and API.

├── components/             - [Learn Next.js](https://nextjs.org/learn) - an interactive Next.js tutorial.

│   ├── auth/               # Auth-related components

│   │   ├── login-form.tsxYou can check out [the Next.js GitHub repository](https://github.com/vercel/next.js) - your feedback and contributions are welcome!

│   │   └── register-form.tsx

│   ├── layout/             # Layout components## Deploy on Vercel

│   │   ├── header.tsx

│   │   └── footer.tsxThe easiest way to deploy your Next.js app is to use the [Vercel Platform](https://vercel.com/new?utm_medium=default-template&filter=next.js&utm_source=create-next-app&utm_campaign=create-next-app-readme) from the creators of Next.js.

│   └── ui/                 # Reusable UI components

│       ├── button.tsxCheck out our [Next.js deployment documentation](https://nextjs.org/docs/app/building-your-application/deploying) for more details.

│       ├── input.tsx
│       ├── label.tsx
│       └── card.tsx
├── lib/                    
│   ├── api/                # API client and services
│   │   ├── client.ts       # Axios instance with interceptors
│   │   ├── config.ts       # API configuration
│   │   ├── auth.service.ts
│   │   ├── vehicle.service.ts
│   │   └── order.service.ts
│   └── utils.ts            # Utility functions
├── store/                  
│   └── auth.store.ts       # Zustand auth store
├── types/                  
│   ├── user.types.ts
│   ├── vehicle.types.ts
│   └── order.types.ts
└── .env.local              # Environment variables
```

## 🛠️ Setup & Installation

### Prerequisites

- Node.js 18+ 
- npm or yarn
- Backend services running (User Service on port 8081, Vehicle Service on 8082, etc.)

### Install Dependencies

```bash
cd frontend
npm install
```

### Environment Variables

Create `.env.local` file:

```env
NEXT_PUBLIC_API_GATEWAY_URL=http://localhost:8080
NEXT_PUBLIC_USER_SERVICE_URL=http://localhost:8081
NEXT_PUBLIC_VEHICLE_SERVICE_URL=http://localhost:8082
NEXT_PUBLIC_ORDER_SERVICE_URL=http://localhost:8083
NEXT_PUBLIC_WORLD_VIEW_URL=http://localhost:8084
```

### Run Development Server

```bash
npm run dev
```

The app will be available at [http://localhost:3000](http://localhost:3000)

## ✅ Implemented Features

### 1. **Authentication (User Service Integration)** ✅

- **Login** (`/auth/login`)
  - Email/password authentication
  - JWT token management
  - Automatic token refresh
  - Form validation with Zod
  
- **Registration** (`/auth/register`)
  - New user sign-up
  - Password confirmation
  - Input validation
  - Automatic login after registration

- **Token Management**
  - Access token stored in localStorage
  - Refresh token for session management
  - Automatic token refresh on 401 errors
  - Axios interceptors for auth headers

### 2. **Layout & Navigation** ✅

- **Header Component**
  - Responsive navigation
  - User profile display when logged in
  - Logout functionality
  - Dynamic navigation links

- **Footer Component**
  - Site-wide footer with links
  - Responsive design

### 3. **Landing Page** ✅

- Hero section with CTAs
- Features showcase
- Responsive design

## 🚧 Upcoming Features

### Phase 2: Vehicle Service UI (Next Priority)

- [ ] **Vehicle Catalog** (`/vehicles`)
  - Grid/List view of vehicles
  - Search and filters (brand, type, price range)
  - Pagination
  - Vehicle cards with images and details

- [ ] **Vehicle Details** (`/vehicles/[id]`)
  - Full vehicle information
  - Image gallery
  - Specifications
  - Customization options
  - Add to cart / Configure button

- [ ] **Vehicle Configuration** (`/vehicles/[id]/configure`)
  - Interactive customization
  - Real-time price updates
  - Option selection
  - Configuration summary

### Phase 3: Order Service UI

- [ ] **Checkout Flow**
  - Order summary
  - Delivery address form
  - Payment integration
  - Order confirmation

- [ ] **Order History** (`/orders`)
  - List of user orders
  - Order status tracking
  - Order details view
  - Cancel order functionality

### Phase 4: World View Integration

- [ ] Real-time order tracking
- [ ] Journey simulation visualization
- [ ] Delivery status updates

## 🎨 UI/UX Features

- **Fully Responsive**: Mobile-first design
- **Dark Mode Ready**: Tailwind dark mode support
- **Accessible**: ARIA labels and keyboard navigation
- **Loading States**: Spinners and skeleton screens
- **Error Handling**: User-friendly error messages
- **Form Validation**: Real-time validation with helpful messages

## 🔧 API Integration

### Auth Service (User Service)

```typescript
// Example usage
import { authService } from '@/lib/api/auth.service';

// Login
const response = await authService.login({
  email: 'user@example.com',
  password: 'password123'
});

// Register
const response = await authService.register({
  email: 'new@example.com',
  password: 'password123',
  firstName: 'John',
  lastName: 'Doe'
});
```

### Vehicle Service (Ready for Implementation)

```typescript
import { vehicleService } from '@/lib/api/vehicle.service';

// Search vehicles
const vehicles = await vehicleService.searchVehicles({
  brandId: 'uuid',
  minPrice: 20000,
  maxPrice: 50000,
  page: 0,
  size: 20
});

// Get vehicle by ID
const vehicle = await vehicleService.getVehicleById('vehicle-uuid');
```

### Order Service (Ready for Implementation)

```typescript
import { orderService } from '@/lib/api/order.service';

// Create order
const order = await orderService.createOrder({
  vehicleId: 'vehicle-uuid',
  configurationId: 'config-uuid',
  deliveryAddress: { /* address */ }
});
```

## 📝 Development Guidelines

### Adding New Pages

1. Create page in `app/` directory
2. Use client components (`"use client"`) for interactive features
3. Import and use API services from `lib/api/`
4. Use Zustand stores for global state

### Creating Components

1. Place in appropriate directory under `components/`
2. Use TypeScript for props
3. Follow naming conventions (PascalCase for components)
4. Use Tailwind classes for styling

### API Integration

1. Define types in `types/` directory
2. Create service functions in `lib/api/`
3. Use Axios interceptors for auth
4. Handle errors gracefully

## 🧪 Testing

```bash
# Run type checking
npm run type-check

# Run linting
npm run lint

# Build for production
npm run build
```

## 🚀 Deployment

```bash
# Build
npm run build

# Start production server
npm start
```

## 📚 Next Steps

1. **Implement Vehicle Catalog UI** - Browse and search vehicles
2. **Vehicle Details Page** - Detailed view with customization
3. **Order Flow** - Complete checkout and order management
4. **Real-time Tracking** - World View integration

## 🤝 Contributing

When implementing new features:

1. Follow the existing code structure
2. Use TypeScript types from `types/` directory
3. Create reusable components in `components/ui/`
4. Add API service functions in `lib/api/`
5. Update this README with new features

## 📞 Support

For issues or questions:
- Check backend API documentation
- Review type definitions in `types/` directory
- Consult API service files in `lib/api/`

---

**Built with ❤️ for BennyCar**
